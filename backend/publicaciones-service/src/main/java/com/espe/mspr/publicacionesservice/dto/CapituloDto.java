package com.espe.mspr.publicacionesservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapituloDto {

    @Min(1)
    private int numero;

    @NotBlank
    private String titulo;

    private String resumenCapitulo;
}