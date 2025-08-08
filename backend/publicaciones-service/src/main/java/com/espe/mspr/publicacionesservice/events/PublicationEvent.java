// events/PublicationEvent.java
package com.espe.mspr.publicacionesservice.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Base para todos los eventos de dominio de publicaciones.
 * Usamos @JsonTypeInfo para serialización polimórfica clara en JSON.
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class PublicationEvent {

    private final UUID eventId = UUID.randomUUID();
    private final String type;
    private final UUID aggregateId;
    private final OffsetDateTime occurredOn = OffsetDateTime.now();
    private final Integer version; // versión de la publicación (no del evento)

    protected PublicationEvent(String type, UUID aggregateId, Integer version) {
        this.type = type;
        this.aggregateId = aggregateId;
        this.version = version;
    }
}
