package com.espe.mspr.publicacionesservice.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearLibroDto {

    // ===== Campos comunes =====
    @NonNull
    private String titulo;

    @NonNull
    private String resumen;

    @Builder.Default
    private List<String> palabrasClave = new ArrayList<>();

    @NonNull
    private UUID autorPrincipalId;

    @Builder.Default
    private List<UUID> coAutoresIds = new ArrayList<>();

    @Builder.Default
    private Map<String, Object> metadatos = new HashMap<>();

    // ===== Específicos de Libro =====
    private String isbn;

    @Min(1)
    private Integer numeroPaginas;

    private String edicion;

    /** Lista de capítulos (se mapeará a @ElementCollection en la entidad) */
    @Builder.Default
    private List<CapituloDto> capitulos = new ArrayList<>();
}
