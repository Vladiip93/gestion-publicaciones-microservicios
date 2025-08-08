package com.espe.mspr.publicacionesservice.services;

import com.espe.mspr.publicacionesservice.dto.CapituloDto;
import com.espe.mspr.publicacionesservice.dto.CrearArticuloDto;
import com.espe.mspr.publicacionesservice.dto.CrearLibroDto;
import com.espe.mspr.publicacionesservice.events.PublicationEvent;
import com.espe.mspr.publicacionesservice.events.PublicationEventFactory;
import com.espe.mspr.publicacionesservice.models.*;
import com.espe.mspr.publicacionesservice.outbox.EventStatus;
import com.espe.mspr.publicacionesservice.outbox.OutboxEvent;
import com.espe.mspr.publicacionesservice.repositories.OutboxEventRepository;
import com.espe.mspr.publicacionesservice.repositories.PublicacionRepository; // <-- asegúrate del paquete correcto
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final PublicationEventFactory eventFactory;

    // ===================== CREACIÓN =====================

    @Transactional
    public Articulo crearArticulo(CrearArticuloDto dto) {
        Articulo art = new Articulo();
        // comunes
        art.setTitulo(dto.getTitulo());
        art.setResumen(dto.getResumen());
        art.setPalabrasClave(nullSafeList(dto.getPalabrasClave()));
        art.setEstado(PublicationState.BORRADOR);
        art.setVersionActual(1);
        art.setAutorPrincipalId(dto.getAutorPrincipalId());
        art.setCoAutoresIds(nullSafeList(dto.getCoAutoresIds()));
        art.setTipo(PublicationType.ARTICULO);
        art.setMetadatos(toJson(dto.getMetadatos()));
        // específicos
        art.setRevistaObjetivo(dto.getRevistaObjetivo());
        art.setSeccion(dto.getSeccion());
        art.setReferenciasBibliograficas(nullSafeList(dto.getReferenciasBibliograficas()));
        art.setFigurasCount(dto.getFigurasCount() == null ? 0 : dto.getFigurasCount());
        art.setTablasMetadata(toJson(dto.getTablasMetadata()));

        return publicacionRepository.save(art);
    }

    @Transactional
    public Libro crearLibro(CrearLibroDto dto) {
        Libro book = new Libro();
        // comunes
        book.setTitulo(dto.getTitulo());
        book.setResumen(dto.getResumen());
        book.setPalabrasClave(nullSafeList(dto.getPalabrasClave()));
        book.setEstado(PublicationState.BORRADOR);
        book.setVersionActual(1);
        book.setAutorPrincipalId(dto.getAutorPrincipalId());
        book.setCoAutoresIds(nullSafeList(dto.getCoAutoresIds()));
        book.setTipo(PublicationType.LIBRO);
        book.setMetadatos(toJson(dto.getMetadatos()));
        // específicos
        book.setIsbn(dto.getIsbn());
        book.setNumeroPaginas(dto.getNumeroPaginas());
        book.setEdicion(dto.getEdicion());
        book.setCapitulos(mapCapitulos(dto.getCapitulos())); // DTO → entidad

        return publicacionRepository.save(book);
    }

    // ===================== TRANSICIONES =====================

    @Transactional
    public Publicacion submitForReview(UUID id) {
        Publicacion p = getOrThrow(id);
        requireState(p, PublicationState.BORRADOR);
        p.setEstado(PublicationState.EN_REVISION);
        publicacionRepository.save(p);

        enqueueEvent(eventFactory.submitted(p));
        enqueueEvent(eventFactory.reviewRequested(p));
        return p;
    }

    @Transactional
    public Publicacion requestChanges(UUID id, String comentarios) {
        Publicacion p = getOrThrow(id);
        requireState(p, PublicationState.EN_REVISION);
        p.setEstado(PublicationState.CAMBIOS_SOLICITADOS);
        publicacionRepository.save(p);

        enqueueEvent(eventFactory.changesRequested(p, comentarios));
        return p;
    }

    /** El autor sube cambios => incrementa versión y vuelve a EN_REVISION. */
    @Transactional
    public Publicacion resubmitAfterChanges(UUID id) {
        Publicacion p = getOrThrow(id);
        requireState(p, PublicationState.CAMBIOS_SOLICITADOS);
        p.setVersionActual(p.getVersionActual() + 1);
        p.setEstado(PublicationState.EN_REVISION);
        publicacionRepository.save(p);

        enqueueEvent(eventFactory.resubmitted(p));
        return p;
    }

    @Transactional
    public Publicacion approve(UUID id) {
        Publicacion p = getOrThrow(id);
        requireState(p, PublicationState.EN_REVISION);
        p.setEstado(PublicationState.APROBADO);
        publicacionRepository.save(p);

        enqueueEvent(eventFactory.approved(p));
        return p;
    }

    @Transactional
    public Publicacion publish(UUID id) {
        Publicacion p = getOrThrow(id);
        requireState(p, PublicationState.APROBADO);
        p.setEstado(PublicationState.PUBLICADO);
        publicacionRepository.save(p);

        // usa SIEMPRE el factory
        enqueueEvent(eventFactory.published(p));
        enqueueEvent(eventFactory.readyForCatalog(p)); // este evento debe incluir el snapshot completo
        return p;
    }

    @Transactional
    public Publicacion retire(UUID id, String motivo) {
        Publicacion p = getOrThrow(id);
        if (p.getEstado() != PublicationState.PUBLICADO) {
            throw new IllegalStateException("Solo se puede RETIRAR una publicación PUBLICADA.");
        }
        p.setEstado(PublicationState.RETIRADO);
        publicacionRepository.save(p);

        enqueueEvent(eventFactory.retired(p, motivo));
        return p;
    }

    // ===================== CONSULTAS =====================

    public Publicacion get(UUID id) {
        return getOrThrow(id);
    }

    // ===================== PRIVADOS =====================

    private Publicacion getOrThrow(UUID id) {
        return publicacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada: " + id));
    }

    private void requireState(Publicacion p, PublicationState expected) {
        if (p.getEstado() != expected) {
            throw new IllegalStateException("Transición inválida. Se esperaba " + expected +
                    " pero está en " + p.getEstado());
        }
    }

    /** Encola un evento de dominio en la Outbox. */
    private void enqueueEvent(PublicationEvent ev) {
        try {
            OutboxEvent e = OutboxEvent.builder()
                    .aggregateId(ev.getAggregateId().toString()) // guardamos UUID como String
                    .eventType(ev.getType())
                    .payload(objectMapper.writeValueAsString(ev)) // todo el evento en JSON
                    .status(EventStatus.PENDIENTE)
                    .fechaCreacion(java.time.LocalDateTime.now())
                    .build();

            outboxEventRepository.save(e);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo encolar evento " + ev.getType(), ex);
        }
    }

    private String toJson(Object obj) {
        try {
            if (obj == null) return null;
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando JSON", e);
        }
    }

    private List<Capitulo> mapCapitulos(List<CapituloDto> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream()
                .map(d -> Capitulo.builder()
                        .numero(d.getNumero())
                        .titulo(d.getTitulo())
                        .resumenCapitulo(d.getResumenCapitulo())
                        .build())
                .collect(Collectors.toList());
    }

    private <T> List<T> nullSafeList(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }
}
