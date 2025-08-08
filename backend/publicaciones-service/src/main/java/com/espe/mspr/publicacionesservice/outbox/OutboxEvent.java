package com.espe.mspr.publicacionesservice.outbox;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "outbox_events")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // o UUID, como prefieras; pero que coincida con la tabla
    private Long id;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;                          // ← String

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(name = "fecha_creacion", nullable = false)
    private java.time.LocalDateTime fechaCreacion;

    @Column(name = "fecha_procesado")
    private java.time.LocalDateTime fechaProcesado;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;
}
