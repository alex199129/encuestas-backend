package com.davivienda.encuestas.repository;

import com.davivienda.encuestas.model.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    List<Respuesta> findByPreguntaId(Long preguntaId);
    List<Respuesta> findByPreguntaEncuestaId(Long encuestaId);
    boolean existsByPreguntaEncuestaId(Long encuestaId);
}
