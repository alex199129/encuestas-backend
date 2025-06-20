package com.davivienda.encuestas.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncuestaResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private String slug;
    private List<PreguntaDTO> preguntas;
}
