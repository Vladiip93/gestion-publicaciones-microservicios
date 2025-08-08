package com.espe.mspr.publicacionesservice.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearArticuloDto {

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

    // ===== Específicos de Artículo =====
    private String revistaObjetivo;
    private String seccion;

    @Builder.Default
    private List<String> referenciasBibliograficas = new ArrayList<>();

    @Min(0)
    @Builder.Default
    private Integer figurasCount = 0;

    /** Metadata de tablas (se serializa a JSONB) */
    @Builder.Default
    private Map<String, Object> tablasMetadata = new HashMap<>();
}
