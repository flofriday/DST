package dst.ass3.messaging.impl;

import com.rabbitmq.http.client.Client;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WorkloadMonitor implements IWorkloadMonitor {


    private Client client;

    private void connect() {
        if (client != null) return;

        try {
            client = new Client(new URL(Constants.RMQ_API_URL), Constants.RMQ_USER, Constants.RMQ_PASSWORD);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Region, Long> getRequestCount() {
        connect();

        var requestCounts = new HashMap<Region, Long>();
        for (var queue : client.getQueues()) {
            var region = Region.valueOf(queue.getName().substring(4).toUpperCase());
            var cnt = queue.getMessagesReady();
            requestCounts.put(region, cnt);
        }

        return requestCounts;
    }

    @Override
    public Map<Region, Long> getWorkerCount() {
        connect();

        var requestCounts = new HashMap<Region, Long>();
        for (var queue : client.getQueues()) {
            var region = Region.valueOf(queue.getName().substring(4).toUpperCase());
            var cnt = queue.getConsumerCount();
            requestCounts.put(region, cnt);
        }

        return requestCounts;
    }

    @Override
    public Map<Region, Double> getAverageProcessingTime() {
        // Ignore for now
        return null;
    }

    @Override
    public void close() throws IOException {
    }
}
