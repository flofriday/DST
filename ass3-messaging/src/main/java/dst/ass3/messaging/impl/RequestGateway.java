package dst.ass3.messaging.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IRequestGateway;
import dst.ass3.messaging.TripRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RequestGateway implements IRequestGateway {

    private Connection connection;
    private Channel channel;

    private void connect() throws IOException, TimeoutException {

        if (connection != null) return;

        var connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constants.RMQ_HOST);
        connectionFactory.setPort(Integer.parseInt(Constants.RMQ_PORT));
        connectionFactory.setUsername(Constants.RMQ_USER);
        connectionFactory.setPassword(Constants.RMQ_PASSWORD);

        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
    }

    @Override
    public void submitRequest(TripRequest request) {
        // Serialize the request
        String message;
        try {
            var mapper = new ObjectMapper();
            message = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Send to the exchnage
        try {
            connect();
            var routingKey = "requests." + request.getRegion().name().toLowerCase();
            channel.basicPublish(Constants.TOPIC_EXCHANGE, routingKey, null, message.getBytes());
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (connection == null) return;

        try {
            channel.close();
        } catch (TimeoutException ignored) {
        }
        connection.close();

        channel = null;
        connection = null;
    }
}
