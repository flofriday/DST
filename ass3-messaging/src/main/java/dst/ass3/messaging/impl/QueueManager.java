package dst.ass3.messaging.impl;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IQueueManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QueueManager implements IQueueManager {

    private Connection conn;

    @Override
    public void setUp() {
        // Connect to RabbitMQ
        var connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constants.RMQ_HOST);
        connectionFactory.setPort(Integer.parseInt(Constants.RMQ_PORT));
        connectionFactory.setUsername(Constants.RMQ_USER);
        connectionFactory.setPassword(Constants.RMQ_PASSWORD);

        try {
            conn = connectionFactory.newConnection();
            try (var channel = conn.createChannel()) {

                channel.exchangeDeclare(Constants.TOPIC_EXCHANGE, "direct");

                // Create the queues
                for (var queue : Constants.WORK_QUEUES) {
                    channel.queueDeclare(queue, false, false, false, null);
                    var routingKey = "request" + queue.substring(queue.indexOf("."));
                    channel.queueBind(queue, Constants.TOPIC_EXCHANGE, routingKey);
                }
            }

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void tearDown() {
        try (var channel = conn.createChannel()) {
            channel.exchangeDelete(Constants.TOPIC_EXCHANGE);

            for (var queue : Constants.WORK_QUEUES) {
                channel.queueDelete(queue);
            }
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (conn != null) conn.close();
    }
}
