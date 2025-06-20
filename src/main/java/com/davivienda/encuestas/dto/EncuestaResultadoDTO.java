package com.davivienda.encuestas.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncuestaResultadoDTO {
    private Long encuestaId;
    private String titulo;
    private List<PreguntaResultadoDTO> preguntas;
}
