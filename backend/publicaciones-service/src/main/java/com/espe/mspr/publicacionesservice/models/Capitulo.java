package com.espe.mspr.publicacionesservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Capitulo {

    private int numero;

    private String titulo;

    @Column(length = 2000)
    private String resumenCapitulo;
}