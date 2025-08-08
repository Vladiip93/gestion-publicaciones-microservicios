// events/PublicationReadyForCatalog.java
package com.espe.mspr.publicacionesservice.events;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class PublicationReadyForCatalog extends PublicationEvent {
    private final String titulo;
    private final String resumen;
    private final List<String> palabrasClave;
    private final String tipo;               // ARTICULO | LIBRO
    private final UUID autorPrincipalId;
    private final List<UUID> coAutoresIds;
    private final Map<String, Object> metadatos; // JSON map

    public PublicationReadyForCatalog(
            UUID aggregateId,
            Integer version,
            String titulo,
            String resumen,
            List<String> palabrasClave,
            String tipo,
            UUID autorPrincipalId,
            List<UUID> coAutoresIds,
            Map<String, Object> metadatos
    ) {
        super("PublicationReadyForCatalog", aggregateId, version);
        this.titulo = titulo;
        this.resumen = resumen;
        this.palabrasClave = palabrasClave;
        this.tipo = tipo;
        this.autorPrincipalId = autorPrincipalId;
        this.coAutoresIds = coAutoresIds;
        this.metadatos = metadatos;
    }
}
