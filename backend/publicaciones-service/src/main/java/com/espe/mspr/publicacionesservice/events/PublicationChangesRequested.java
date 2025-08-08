package com.espe.mspr.publicacionesservice.events;

import lombok.Getter;
import java.util.UUID;

@Getter
public class PublicationChangesRequested extends PublicationEvent {
    private final String comentarios;

    public PublicationChangesRequested(UUID aggregateId, Integer version, String comentarios) {
        super("PublicationChangesRequested", aggregateId, version);
        this.comentarios = comentarios;
    }
}
