package com.espe.mspr.publicacionesservice.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "articulos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
@DiscriminatorValue("ARTICULO")
public class Articulo extends Publicacion {

    private String revistaObjetivo;

    private String seccion;

    @ElementCollection
    @CollectionTable(
            name = "articulo_referencias",
            joinColumns = @JoinColumn(name = "publicacion_id")
    )
    @Column(name = "referencia")
    private List<String> referenciasBibliograficas;

    private int figurasCount;

    @Column(columnDefinition = "jsonb")
    private String tablasMetadata;
}
