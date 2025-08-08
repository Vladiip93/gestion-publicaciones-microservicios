// events/PublicationEventFactory.java
package com.espe.mspr.publicacionesservice.events;

import com.espe.mspr.publicacionesservice.models.Publicacion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PublicationEventFactory {

    private final ObjectMapper objectMapper;

    public PublicationSubmitted submitted(Publicacion p) {
        return new PublicationSubmitted(p.getId(), p.getVersionActual());
    }

    public ReviewRequested reviewRequested(Publicacion p) {
        return new ReviewRequested(p.getId(), p.getVersionActual());
    }

    public PublicationApproved approved(Publicacion p) {
        return new PublicationApproved(p.getId(), p.getVersionActual());
    }

    public PublicationPublished published(Publicacion p) {
        return new PublicationPublished(p.getId(), p.getVersionActual());
    }

    public PublicationResubmitted resubmitted(Publicacion p) {
        return new PublicationResubmitted(p.getId(), p.getVersionActual());
    }

    public PublicationRetired retired(Publicacion p, String motivo) {
        return new PublicationRetired(p.getId(), p.getVersionActual(), motivo);
    }

    public PublicationChangesRequested changesRequested(Publicacion p, String comentarios) {
        return new PublicationChangesRequested(p.getId(), p.getVersionActual(), comentarios);
    }

    public PublicationReadyForCatalog readyForCatalog(Publicacion p) {
        Map<String, Object> meta = parseJsonMap(p.getMetadatos());
        return new PublicationReadyForCatalog(
                p.getId(),
                p.getVersionActual(),
                p.getTitulo(),
                p.getResumen(),
                p.getPalabrasClave(),
                p.getTipo().name(),
                p.getAutorPrincipalId(),
                p.getCoAutoresIds(),
                meta
        );
    }

    private Map<String, Object> parseJsonMap(String json) {
        try {
            if (json == null || json.isBlank()) return Map.of();
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of(); // evitamos romper por metadatos mal formados
        }
    }
}
