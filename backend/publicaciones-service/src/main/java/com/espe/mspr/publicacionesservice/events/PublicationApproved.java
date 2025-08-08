package com.espe.mspr.publicacionesservice.events;

import java.util.UUID;

public class PublicationApproved extends PublicationEvent {
    public PublicationApproved(UUID aggregateId, Integer version) {
        super("PublicationApproved", aggregateId, version);
    }
}
