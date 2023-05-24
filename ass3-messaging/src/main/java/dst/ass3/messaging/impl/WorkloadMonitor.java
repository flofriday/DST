package dst.ass3.messaging.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.http.client.Client;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;
import dst.ass3.messaging.WorkerResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class WorkloadMonitor implements IWorkloadMonitor {

    private Client client;
    private Connection connection;
    private Channel channel;
    private String queue;
    private Map<Region, List<Long>> processintTimes = new HashMap<>();

    public WorkloadMonitor() {
        if (client != null) return;

        try {
            client = new Client(new URL(Constants.RMQ_API_URL), Constants.RMQ_USER, Constants.RMQ_PASSWORD);

            var connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(Constants.RMQ_HOST);
            connectionFactory.setPort(Integer.parseInt(Constants.RMQ_PORT));
            connectionFactory.setUsername(Constants.RMQ_USER);
            connectionFactory.setPassword(Constants.RMQ_PASSWORD);

            connection = connectionFactory.newConnection();
            channel = connection.createChannel();

            queue = channel.queueDeclare("", false, false, false, null).getQueue();
            for (var workQueue : Constants.WORK_QUEUES) {
                var topic = "requests" + workQueue.substring(queue.indexOf("."));
                channel.queueBind(queue, Constants.TOPIC_EXCHANGE, topic);
            }

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                var deliverYKey = delivery.getEnvelope().getRoutingKey();
                var region = Region.valueOf(deliverYKey.substring(deliverYKey.indexOf(".") + 1).toUpperCase());
                processMessage(message, region);
            };
            channel.basicConsume(queue, true, deliverCallback, consuerTag -> {
            });

        } catch (URISyntaxException | IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized private void processMessage(String message, Region region) {
        System.out.println(message);
        var mapper = new ObjectMapper();
        WorkerResponse response;
        try {
            response = mapper.readValue(message, WorkerResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (!processintTimes.containsKey(region)) {
            processintTimes.put(region, new LinkedList<Long>());
        }
        var list = processintTimes.get(region);
        list.add(response.getProcessingTime());
        while (list.size() > 10)
            list.remove(0);
        processintTimes.put(region, list);

    }

    @Override
    public Map<Region, Long> getRequestCount() {

        var requestCounts = new HashMap<Region, Long>();
        for (var queue : client.getQueues()) {
            if (!List.of(Constants.WORK_QUEUES).contains(queue.getName())) continue;

            var region = Region.valueOf(queue.getName().substring(4).toUpperCase());
            var cnt = queue.getMessagesReady();
            requestCounts.put(region, cnt);
        }

        return requestCounts;
    }

    @Override
    public Map<Region, Long> getWorkerCount() {

        var requestCounts = new HashMap<Region, Long>();
        for (var queue : client.getQueues()) {
            if (!List.of(Constants.WORK_QUEUES).contains(queue.getName())) continue;

            var region = Region.valueOf(queue.getName().substring(4).toUpperCase());
            var cnt = queue.getConsumerCount();
            requestCounts.put(region, cnt);
        }

        return requestCounts;
    }

    @Override
    synchronized public Map<Region, Double> getAverageProcessingTime() {

        var map = new HashMap<Region, Double>();
        for (var pair : processintTimes.entrySet()) {
            map.put(pair.getKey(), pair.getValue().stream().mapToDouble(t -> t).average().orElse(0.0));
        }
        return map;
    }

    @Override
    public void close() throws IOException {
        if (client == null) return;

        channel.queueDelete(queue);
        queue = null;

        try {
            channel.close();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        channel = null;

        connection.close();
        connection = null;
    }
}
