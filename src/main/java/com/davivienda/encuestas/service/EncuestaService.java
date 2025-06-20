package com.davivienda.encuestas.service;

import com.davivienda.encuestas.dto.CrearEncuestaRequest;
import com.davivienda.encuestas.dto.EncuestaPublicaResponse;
import com.davivienda.encuestas.dto.EncuestaResponse;
import com.davivienda.encuestas.dto.EncuestaResultadoDTO;
import com.davivienda.encuestas.dto.RespuestaRequest;

import java.util.List;

public interface EncuestaService {

    EncuestaResponse crearEncuesta(CrearEncuestaRequest request, String emailUsuario);

    List<EncuestaResponse> listarEncuestasUsuario(String emailUsuario);

    EncuestaResponse obtenerPorId(Long id);

    EncuestaResponse actualizarEncuesta(Long id, CrearEncuestaRequest request, String emailUsuario);

    void eliminarEncuesta(Long id, String emailUsuario);
    
    EncuestaPublicaResponse obtenerEncuestaPublica(String slug);

    void guardarRespuestas(String slug, List<RespuestaRequest> respuestas);
    
    boolean encuestaTieneRespuestas(Long encuestaId);
    
    EncuestaResultadoDTO obtenerResultados(Long encuestaId, String emailUsuario);
    
}
