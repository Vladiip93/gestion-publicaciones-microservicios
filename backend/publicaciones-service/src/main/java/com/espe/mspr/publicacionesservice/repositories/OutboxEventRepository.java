package com.espe.mspr.publicacionesservice.repositories;

import com.espe.mspr.publicacionesservice.outbox.EventStatus;
import com.espe.mspr.publicacionesservice.outbox.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    // útil para consultas simples
    List<OutboxEvent> findByStatus(EventStatus status);

    // *** método que usa el Dispatcher (con Pageable) ***
    List<OutboxEvent> findByStatusOrderByFechaCreacionAsc(EventStatus status, Pageable pageable);

    // opcional: alternativa sin Pageable
    List<OutboxEvent> findTop50ByStatusOrderByFechaCreacionAsc(EventStatus status);
}
