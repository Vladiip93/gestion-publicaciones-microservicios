package com.espe.mspr.publicacionesservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "publicaciones")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Publicacion {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name= "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String resumen;

    @ElementCollection
    @CollectionTable(
            name = "publicacion_palabras_clave",
            joinColumns = @JoinColumn(name = "publicacion_id")
    )
    @Column(name = "palabra")
    private List<String> palabrasClave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublicationState estado;

    @Column(nullable = false)
    private int versionActual;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @Column(nullable = false)
    private UUID autorPrincipalId;

    @ElementCollection
    @CollectionTable(
            name = "publicacion_coautores",
            joinColumns = @JoinColumn(name = "publicacion_id")
    )
    @Column(name = "coautor_id")
    private List<UUID> coAutoresIds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublicationType tipo;

    @Column(columnDefinition = "jsonb")
    private String metadatos;
}
