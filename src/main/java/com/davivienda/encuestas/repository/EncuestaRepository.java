package com.davivienda.encuestas.repository;

import com.davivienda.encuestas.model.Encuesta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface EncuestaRepository extends JpaRepository<Encuesta, Long> {
    Optional<Encuesta> findBySlug(String slug);
    List<Encuesta> findByCreadorId(Long creadorId);
}
