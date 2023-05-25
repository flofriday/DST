import json
import random
import signal
import sys
import time
from dataclasses import dataclass
from math import radians, sin, cos, atan2, sqrt

import pika
import redis
from pika.adapters.blocking_connection import BlockingChannel


@dataclass
class Location:
    longitude: float
    latitude: float


@dataclass
class MatchRequest:
    id: str
    pickup: Location


@dataclass
class Driver:
    id: str
    location: Location


def fetch_drivers(region) -> list[Driver]:
    r = redis.Redis()
    all = r.hgetall("drivers:" + region)
    drivers = []
    for k, v in all.items():
        k, v = k.decode(), v.decode()
        lon, lat = v.split(" ")
        drivers.append(Driver(k, Location(float(lon), float(lat))))

    return drivers


def distance(start: Location, end: Location) -> float:
    lat1 = radians(start.latitude)
    lon1 = radians(start.longitude)
    lat2 = radians(end.latitude)
    lon2 = radians(start.longitude)

    dlon = lon2 - lon1
    dlat = lat2 - lat1

    a = sin(dlat / 2) ** 2 + cos(lat1) * cos(lat2) * sin(dlon / 2) ** 2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))

    R = 6373.0
    return R * c


def match(request: MatchRequest, drivers: list[Driver]) -> Driver:
    return min(drivers, key=lambda d: distance(d.location, request.pickup))


def simulate_work(region):
    random_sleeps = {
        "at_linz": (1, 2),
        "at_vienna": (3, 5),
        "de_berlin": (9, 11),
    }

    lower, upper = random_sleeps[region]
    time.sleep(random.randint(lower, upper))


def remove_driver(driver: Driver, region: str) -> int:
    r = redis.Redis()
    return r.hdel("drivers:" + region, str(driver.id))


def publish(ch: BlockingChannel, req: MatchRequest, matched_driver: Driver, region: str, duration_ms: int):
    message = {
        "requestId": req.id,
        "driverId": matched_driver.id,
        "processingTime": duration_ms,
    }
    ch.basic_publish("dst.workers", "requests." + region, json.dumps(message).encode())


def callback(ch, method, properties, body, region):
    print("callback")
    while True:
        start_ms = time.time_ns() / 1000
        message = json.loads(body)
        req = MatchRequest(message['id'], Location(message['pickup']['longitude'], message['pickup']['longitude']))
        print(f"Start matching for {req.id}")
        drivers = fetch_drivers(region)
        if drivers == []:
            print("No driver available")
            publish(ch, req, Driver("", None), region, 0)
            break

        print(f"Got {len(drivers)} available drivers")
        matched_driver = match(req, drivers)
        simulate_work(region)
        print(f"Matched driver {matched_driver.id}")
        removed = remove_driver(matched_driver, region)
        if removed == 0:
            print("Recalculate a match, driver no longer available")
            continue

        duration_ms = (time.time_ns() / 1000) - start_ms
        publish(ch, req, matched_driver, region, int(duration_ms))
        print(f"Published result")
        break


def main():
    if len(sys.argv) != 2:
        print("Usage: worker.py REGION")
        exit(1)

    region = sys.argv[1]

    connection = pika.BlockingConnection(pika.ConnectionParameters(
        'localhost',
        5672,
        '/',
        pika.PlainCredentials('dst', 'dst')))
    channel = connection.channel()
    queue = "dst." + region
    channel.queue_declare(queue)
    channel.basic_consume(queue=queue,
                          on_message_callback=lambda c, m, p, b: callback(c, m, p, b, region),
                          auto_ack=True)

    def term_handler():
        print("Terminiated with SIGTERM")
        sys.exit(0)

    signal.signal(signal.SIGTERM, term_handler)

    print("Waiting for messages")
    channel.start_consuming()


if __name__ == "__main__":
    main()
