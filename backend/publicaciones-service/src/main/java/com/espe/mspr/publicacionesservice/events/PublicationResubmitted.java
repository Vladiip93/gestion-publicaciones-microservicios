package com.espe.mspr.publicacionesservice.events;

import java.util.UUID;

public class PublicationResubmitted extends PublicationEvent {
    public PublicationResubmitted(UUID aggregateId, Integer version) {
        super("PublicationResubmitted", aggregateId, version);
    }
}
