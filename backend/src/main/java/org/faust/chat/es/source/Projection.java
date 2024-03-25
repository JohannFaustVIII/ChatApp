package org.faust.chat.es.source;

import org.faust.chat.es.event.Event;
import org.faust.chat.es.event.EventMetatype;
import reactor.core.publisher.Flux;

// TODO: CQRS + event streaming + reactive streams? It may be wrong?
public abstract class Projection {

    private final EventMetatype eventMetatype;

    private long version = 0;

    protected Projection(EventMetatype eventMetatype) {
        this.eventMetatype = eventMetatype;
    }

    public void load(Flux<Event> events) {
        events.subscribe(event -> {
            validate(event);
            when(event.getEvent());
            this.version = event.getVersion();
        });
    }

}
