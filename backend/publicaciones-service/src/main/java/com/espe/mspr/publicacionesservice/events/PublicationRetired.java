package com.espe.mspr.publicacionesservice.events;

import lombok.Getter;
import java.util.UUID;

@Getter
public class PublicationRetired extends PublicationEvent {
    private final String motivo;

    public PublicationRetired(UUID aggregateId, Integer version, String motivo) {
        super("PublicationRetired", aggregateId, version);
        this.motivo = motivo;
    }
}
