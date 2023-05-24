package dst.ass3.messaging.impl;

import dst.ass3.messaging.IMessagingFactory;
import dst.ass3.messaging.IQueueManager;
import dst.ass3.messaging.IRequestGateway;
import dst.ass3.messaging.IWorkloadMonitor;

public class MessagingFactory implements IMessagingFactory {

    @Override
    public IQueueManager createQueueManager() {
        return new QueueManager();
    }

    @Override
    public IRequestGateway createRequestGateway() {
        return new RequestGateway();
    }

    @Override
    public IWorkloadMonitor createWorkloadMonitor() {
        return new WorkloadMonitor();
    }

    @Override
    public void close() {
        // implement if needed
    }
}
