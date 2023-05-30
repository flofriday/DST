package dst.ass3.event;

import dst.ass3.event.impl.EventSourceFunction;

/**
 * Creates your {@link IEventProcessingEnvironment} and {@link IEventSourceFunction} implementation instances.
 */
public class EventProcessingFactory {
    public static IEventProcessingEnvironment createEventProcessingEnvironment() {
        // TODO
        return null;
    }

    public static IEventSourceFunction createEventSourceFunction() {
        return new EventSourceFunction();

    }
}
