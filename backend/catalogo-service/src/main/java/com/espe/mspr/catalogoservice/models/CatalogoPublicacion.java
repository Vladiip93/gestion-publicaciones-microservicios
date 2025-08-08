package com.espe.mspr.catalogoservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "catalogo_publicacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogoPublicacion {

    /** Usamos el ID de la publicación origen como PK para evitar duplicados. */
    @Id
    @Column(name = "publicacion_id", nullable = false, updatable = false)
    private UUID publicacionId;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String resumen;

    /** ARTICULO | LIBRO */
    @Column(nullable = false)
    private String tipo;

    /** Para filtros por autor si lo necesitas en el futuro */
    @Column(name = "autor_principal_id")
    private UUID autorPrincipalId;

    private String isbn;
    private String doi;

    /** Palabras clave indexables */
    @ElementCollection
    @CollectionTable(
            name = "catalogo_palabras_clave",
            joinColumns = @JoinColumn(name = "publicacion_id")
    )
    @Column(name = "palabra")
    @Builder.Default
    private List<String> palabras = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "fecha_indexado", updatable = false)
    private LocalDateTime fechaIndexado;
}
