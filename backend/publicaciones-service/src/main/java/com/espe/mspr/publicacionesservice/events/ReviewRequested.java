package com.espe.mspr.publicacionesservice.events;

import java.util.UUID;

public class ReviewRequested extends PublicationEvent {
    public ReviewRequested(UUID aggregateId, Integer version) {
        super("ReviewRequested", aggregateId, version);
    }
}
