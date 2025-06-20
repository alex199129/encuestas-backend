package com.davivienda.encuestas.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestasEncuestaRequest {
    private List<RespuestaRequest> respuestas;
}
