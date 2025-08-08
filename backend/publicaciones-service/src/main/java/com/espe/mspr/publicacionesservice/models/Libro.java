package com.espe.mspr.publicacionesservice.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@DiscriminatorValue("LIBRO") // si usas herencia TABLE_PER_CLASS cambia esto
public class Libro extends Publicacion {

    private String isbn;

    private Integer numeroPaginas;

    private String edicion;

    @ElementCollection
    @CollectionTable(
            name = "libro_capitulos",
            joinColumns = @JoinColumn(name = "publicacion_id")
    )
    @OrderBy("numero ASC")
    private List<Capitulo> capitulos = new ArrayList<>();
}
