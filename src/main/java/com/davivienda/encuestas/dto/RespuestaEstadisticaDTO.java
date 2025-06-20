package com.davivienda.encuestas.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaEstadisticaDTO {
    private String valor;
    private Long cantidad; // para conteo (null si tipo texto libre)
}
