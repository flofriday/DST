# To build: docker build -t dst/ass3-worker .
# To run: docker run --rm --network=dst dst/ass3-worker at_vienna
FROM python:3-slim

WORKDIR /app

# I really which we could use requirements.txt
RUN pip install pika redis

COPY worker.py .

ENTRYPOINT [ "python", "./worker.py" ]