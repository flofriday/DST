package dst.ass3.event.impl;

import dst.ass3.event.Constants;
import dst.ass3.event.EventPublisher;
import dst.ass3.event.EventSubscriber;
import dst.ass3.event.IEventSourceFunction;
import dst.ass3.event.model.domain.ITripEventInfo;
import org.apache.flink.api.common.functions.IterationRuntimeContext;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;

import java.net.InetSocketAddress;

public class EventSourceFunction implements IEventSourceFunction {

    private boolean isRunning = false;
    private EventSubscriber eventSubscriber;
    private EventPublisher eventPublisher;
    private RuntimeContext runtimeContext;

    @Override
    public void open(Configuration parameters) throws Exception {
        // Setup all reassources
        eventSubscriber = EventSubscriber.subscribe(new InetSocketAddress(Constants.EVENT_PUBLISHER_PORT));
        eventPublisher = new EventPublisher(Constants.EVENT_PUBLISHER_PORT);
    }

    @Override
    public void close() throws Exception {
        // Close all ressources
        eventSubscriber.close();
        eventSubscriber = null;

        eventPublisher.close();
        eventPublisher = null;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public IterationRuntimeContext getIterationRuntimeContext() {
        return null;
    }

    @Override
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public void run(SourceContext<ITripEventInfo> ctx) throws Exception {
        isRunning = true;
        while (isRunning) {
            var eventInfo = eventSubscriber.receive();
            if (eventInfo == null) {
                isRunning = false;
                break;
            }
            ctx.collectWithTimestamp(eventInfo, eventInfo.getTimestamp());
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
