package com.espe.mspr.publicacionesservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearPublicacionBaseDto {

    @NotBlank
    private String titulo;

    @NotBlank
    private String resumen;

    @Builder.Default
    @Size(max = 50)
    private List<String> palabrasClave = new ArrayList<>();

    @NotNull
    private UUID autorPrincipalId;

    @Builder.Default
    private List<UUID> coAutoresIds = new ArrayList<>();

    /**
     * Metadatos arbitrarios (se serializa a JSONB en la entidad).
     * Ej: { "doi":"10.1234/abc", "categoria":"CS", "licencia":"CC-BY" }
     */
    @Builder.Default
    private Map<String, Object> metadatos = new HashMap<>();
}