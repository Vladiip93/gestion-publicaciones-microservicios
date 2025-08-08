package com.espe.mspr.catalogoservice.services;

import com.espe.mspr.catalogoservice.models.CatalogoPublicacion;
import com.espe.mspr.catalogoservice.repositories.CatalogoPublicacionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicationEventListener {

    private final CatalogoPublicacionRepository repo;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handlePublicationReadyForCatalog(String message) {
        try {
            JsonNode payload = objectMapper.readTree(message);

            UUID publicacionId = UUID.fromString(payload.path("id").asText());

            // Construimos el snapshot recibido
            CatalogoPublicacion nuevo = new CatalogoPublicacion();
            nuevo.setPublicacionId(publicacionId);
            nuevo.setTitulo(payload.path("titulo").asText(null));
            nuevo.setResumen(payload.path("resumen").asText(null));
            nuevo.setTipo(payload.path("tipo").asText(null));

            if (payload.hasNonNull("autorPrincipalId")) {
                nuevo.setAutorPrincipalId(UUID.fromString(payload.get("autorPrincipalId").asText()));
            }
            // isbn / doi si llegan en tu snapshot:
            if (payload.hasNonNull("isbn")) nuevo.setIsbn(payload.get("isbn").asText());
            if (payload.hasNonNull("doi"))  nuevo.setDoi(payload.get("doi").asText());

            List<String> palabras = new ArrayList<>();
            if (payload.has("palabrasClave") && payload.get("palabrasClave").isArray()) {
                for (JsonNode n : payload.get("palabrasClave")) {
                    palabras.add(n.asText());
                }
            }
            nuevo.setPalabras(palabras);

            // UPSERT por clave primaria (publicacionId)
            Optional<CatalogoPublicacion> existingOpt = repo.findById(publicacionId);
            if (existingOpt.isPresent()) {
                CatalogoPublicacion ex = existingOpt.get();
                ex.setTitulo(nuevo.getTitulo());
                ex.setResumen(nuevo.getResumen());
                ex.setTipo(nuevo.getTipo());
                ex.setAutorPrincipalId(nuevo.getAutorPrincipalId());
                ex.setIsbn(nuevo.getIsbn());
                ex.setDoi(nuevo.getDoi());
                ex.setPalabras(nuevo.getPalabras());
                // OJO: fechaIndexado se mantiene (es @CreationTimestamp)
                repo.save(ex);
                log.info("Catálogo actualizado para publicacionId={}", publicacionId);
            } else {
                // fechaIndexado se setea sola por @CreationTimestamp
                repo.save(nuevo);
                log.info("Catálogo insertado para publicacionId={}", publicacionId);
            }
        } catch (Exception e) {
            log.error("Error procesando mensaje de catálogo", e);
        }
    }
}
