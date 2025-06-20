package com.davivienda.encuestas.repository;

import com.davivienda.encuestas.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByEncuestaId(Long encuestaId);
}
