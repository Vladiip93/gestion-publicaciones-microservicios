package com.espe.mspr.publicacionesservice.events;

import java.util.UUID;

public class PublicationPublished extends PublicationEvent {
    public PublicationPublished(UUID aggregateId, Integer version) {
        super("PublicationPublished", aggregateId, version);
    }
}
