package com.espe.mspr.publicacionesservice.controllers;

import com.espe.mspr.publicacionesservice.dto.CrearArticuloDto;
import com.espe.mspr.publicacionesservice.dto.CrearLibroDto;
import com.espe.mspr.publicacionesservice.models.Articulo;
import com.espe.mspr.publicacionesservice.models.Libro;
import com.espe.mspr.publicacionesservice.models.Publicacion;
import com.espe.mspr.publicacionesservice.services.PublicacionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/publications", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
// Descomenta si vas directo desde el front sin el API Gateway
@CrossOrigin(origins = "http://localhost:63342")
public class PublicacionController {

    private final PublicacionService publicacionService;

    // ========================
    // Crear
    // ========================

    @PostMapping(value = "/articles", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createArticle(@Valid @RequestBody CrearArticuloDto dto) {
        Articulo saved = publicacionService.crearArticulo(dto);
        return ResponseEntity
                .created(URI.create("/api/publications/" + saved.getId()))
                .body(saved);
    }

    @PostMapping(value = "/books", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBook(@Valid @RequestBody CrearLibroDto dto) {
        Libro saved = publicacionService.crearLibro(dto);
        return ResponseEntity
                .created(URI.create("/api/publications/" + saved.getId()))
                .body(saved);
    }

    // ========================
    // Consultar
    // ========================

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            Publicacion p = publicacionService.get(id);
            return ResponseEntity.ok(p);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========================
    // Transiciones de estado
    // ========================

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitForReview(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(publicacionService.submitForReview(id));
        } catch (IllegalStateException | EntityNotFoundException e) {
            return badRequest(e);
        }
    }

    @PostMapping(value = "/{id}/request-changes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> requestChanges(@PathVariable UUID id,
                                            @RequestBody(required = false) Map<String, String> body) {
        String comentarios = body != null ? body.getOrDefault("comentarios", "") : "";
        try {
            return ResponseEntity.ok(publicacionService.requestChanges(id, comentarios));
        } catch (IllegalStateException | EntityNotFoundException e) {
            return badRequest(e);
        }
    }

    @PostMapping("/{id}/resubmit")
    public ResponseEntity<?> resubmitAfterChanges(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(publicacionService.resubmitAfterChanges(id));
        } catch (IllegalStateException | EntityNotFoundException e) {
            return badRequest(e);
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(publicacionService.approve(id));
        } catch (IllegalStateException | EntityNotFoundException e) {
            return badRequest(e);
        }
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publish(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(publicacionService.publish(id));
        } catch (IllegalStateException | EntityNotFoundException e) {
            return badRequest(e);
        }
    }

    @PostMapping(value = "/{id}/retire", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> retire(@PathVariable UUID id,
                                    @RequestBody(required = false) Map<String, String> body) {
        String motivo = body != null ? body.getOrDefault("motivo", "") : "";
        try {
            return ResponseEntity.ok(publicacionService.retire(id, motivo));
        } catch (IllegalStateException | EntityNotFoundException e) {
            return badRequest(e);
        }
    }

    // ========================
    // Helpers
    // ========================
    private ResponseEntity<?> badRequest(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
