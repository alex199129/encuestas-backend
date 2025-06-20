package com.davivienda.encuestas.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearEncuestaRequest {
    private String titulo;
    private String descripcion;
    private List<PreguntaDTO> preguntas;
}
