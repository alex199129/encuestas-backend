package com.davivienda.encuestas.dto;

import com.davivienda.encuestas.model.Pregunta.Tipo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreguntaResultadoDTO {
    private Long id;
    private String texto;
    private Tipo tipo;
    private List<RespuestaEstadisticaDTO> respuestas;
}
