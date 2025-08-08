// events/PublicationSubmitted.java
package com.espe.mspr.publicacionesservice.events;

import java.util.UUID;

public class PublicationSubmitted extends PublicationEvent {
    public PublicationSubmitted(UUID aggregateId, Integer version) {
        super("PublicationSubmitted", aggregateId, version);
    }
}