package com.davivienda.encuestas.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaRequest {
    private Long preguntaId;
    private String valor;
}
