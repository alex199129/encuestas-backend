package com.davivienda.encuestas.service.impl;

import com.davivienda.encuestas.dto.CrearEncuestaRequest;
import com.davivienda.encuestas.dto.EncuestaPublicaResponse;
import com.davivienda.encuestas.dto.EncuestaResponse;
import com.davivienda.encuestas.dto.EncuestaResultadoDTO;
import com.davivienda.encuestas.dto.PreguntaDTO;
import com.davivienda.encuestas.dto.OpcionDTO;
import com.davivienda.encuestas.dto.PreguntaResultadoDTO;
import com.davivienda.encuestas.dto.RespuestaEstadisticaDTO;
import com.davivienda.encuestas.dto.RespuestaRequest;
import com.davivienda.encuestas.model.Encuesta;
import com.davivienda.encuestas.model.Pregunta;
import com.davivienda.encuestas.model.Respuesta;
import com.davivienda.encuestas.model.Usuario;
import com.davivienda.encuestas.model.Opcion;
import com.davivienda.encuestas.repository.EncuestaRepository;
import com.davivienda.encuestas.repository.PreguntaRepository;
import com.davivienda.encuestas.repository.RespuestaRepository;
import com.davivienda.encuestas.repository.UsuarioRepository;
import com.davivienda.encuestas.service.EncuestaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class EncuestaServiceImpl implements EncuestaService {

    private final EncuestaRepository encuestaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PreguntaRepository preguntaRepository;
    private final RespuestaRepository respuestaRepository;

    @Override
    public EncuestaResponse crearEncuesta(CrearEncuestaRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Encuesta encuesta = Encuesta.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .slug(generarSlug(request.getTitulo()))
                .creador(usuario)
                .build();

        encuesta = encuestaRepository.save(encuesta);
        final Encuesta encuestaFinal = encuesta;
        
        List<Pregunta> preguntas = request.getPreguntas().stream().map(p -> {
            Pregunta pregunta = Pregunta.builder()
                .texto(p.getTexto())
                .tipo(p.getTipo())
                .encuesta(encuestaFinal)
                .build();

            if (p.getOpciones() != null) {
                List<Opcion> opciones = p.getOpciones().stream()
                    .map(o -> Opcion.builder()
                        .texto(o.getTexto())
                        .pregunta(pregunta) // importante
                        .build())
                    .toList();
                pregunta.setOpciones(opciones);
            }

            return pregunta;
        }).toList();


        preguntaRepository.saveAll(preguntas);

        encuesta.setPreguntas(preguntas);

        return toResponse(encuesta);
    }

    @Override
    public List<EncuestaResponse> listarEncuestasUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Encuesta> encuestas = encuestaRepository.findByCreadorId(usuario.getId());
        return encuestas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public EncuestaResponse obtenerPorId(Long id) {
        Encuesta encuesta = encuestaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));
        return toResponse(encuesta);
    }

    @Override
    public EncuestaResponse actualizarEncuesta(Long id, CrearEncuestaRequest request, String emailUsuario) {
        Encuesta encuesta = encuestaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

        if (!encuesta.getCreador().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No autorizado para actualizar esta encuesta");
        }

        // Actualiza los campos básicos
        encuesta.setTitulo(request.getTitulo());
        encuesta.setDescripcion(request.getDescripcion());

        // Elimina las preguntas actuales de la encuesta
        encuesta.getPreguntas().clear();

        // Crea nuevas preguntas y las asigna
        List<Pregunta> nuevasPreguntas = request.getPreguntas().stream().map(p -> {
            Pregunta pregunta = Pregunta.builder()
                .texto(p.getTexto())
                .tipo(p.getTipo())
                .encuesta(encuesta)
                .build();

            if (p.getOpciones() != null) {
                List<Opcion> opciones = p.getOpciones().stream()
                    .map(o -> Opcion.builder()
                        .texto(o.getTexto())
                        .pregunta(pregunta)
                        .build())
                    .toList();
                pregunta.setOpciones(opciones);
            }

            return pregunta;
        }).toList();

        // Asigna las nuevas preguntas directamente
        encuesta.getPreguntas().addAll(nuevasPreguntas);

        // Guarda la encuesta con las preguntas actualizadas
        encuestaRepository.save(encuesta);

        return toResponse(encuesta);
    }


    @Override
    public void eliminarEncuesta(Long id, String emailUsuario) {
        Encuesta encuesta = encuestaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

        if (!encuesta.getCreador().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No autorizado para eliminar esta encuesta");
        }

        encuestaRepository.delete(encuesta);
    }

    // -------------------------
    // Métodos auxiliares
    // -------------------------

    private EncuestaResponse toResponse(Encuesta encuesta) {
        List<PreguntaDTO> preguntas = encuesta.getPreguntas() != null
            ? encuesta.getPreguntas().stream().map(p -> {
                List<OpcionDTO> opciones = p.getOpciones() != null
                    ? p.getOpciones().stream()
                        .map(o -> OpcionDTO.builder()
                            .id(o.getId())
                            .texto(o.getTexto())
                            .build())
                        .toList()
                    : List.of();

                return PreguntaDTO.builder()
                    .id(p.getId())
                    .texto(p.getTexto())
                    .tipo(p.getTipo())
                    .opciones(opciones)
                    .build();
            }).toList()
            : List.of();

        return EncuestaResponse.builder()
                .id(encuesta.getId())
                .titulo(encuesta.getTitulo())
                .descripcion(encuesta.getDescripcion())
                .slug(encuesta.getSlug())
                .preguntas(preguntas)
                .build();
    }


    private String generarSlug(String titulo) {
        String normalizado = Normalizer.normalize(titulo, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("[^a-zA-Z0-9]", "-")
                .toLowerCase();

        return normalizado + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public EncuestaPublicaResponse obtenerEncuestaPublica(String slug) {
        Encuesta encuesta = encuestaRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

        List<PreguntaDTO> preguntas = encuesta.getPreguntas().stream()
        		.map(p -> {
        		    List<OpcionDTO> opciones = p.getOpciones() != null
        		        ? p.getOpciones().stream()
        		            .map(o -> OpcionDTO.builder()
        		                .id(o.getId())
        		                .texto(o.getTexto())
        		                .build())
        		            .toList()
        		        : List.of();

        		    return PreguntaDTO.builder()
        		        .id(p.getId())
        		        .texto(p.getTexto())
        		        .tipo(p.getTipo())
        		        .opciones(opciones)
        		        .build();
        		})
        	    .toList();

        return EncuestaPublicaResponse.builder()
                .titulo(encuesta.getTitulo())
                .descripcion(encuesta.getDescripcion())
                .preguntas(preguntas)
                .build();
    }


    @Override
    public void guardarRespuestas(String slug, List<RespuestaRequest> respuestas) {
        Encuesta encuesta = encuestaRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

        List<Respuesta> entidades = respuestas.stream().map(r -> Respuesta.builder()
                .valor(r.getValor())
                .pregunta(preguntaRepository.findById(r.getPreguntaId())
                        .orElseThrow(() -> new RuntimeException("Pregunta no encontrada")))
                .tokenUsuarioAnonimo(UUID.randomUUID().toString()) 
                .build()
        ).toList();

        respuestaRepository.saveAll(entidades);
    }

    @Override
    public boolean encuestaTieneRespuestas(Long encuestaId) {
        return respuestaRepository.existsByPreguntaEncuestaId(encuestaId);
    }
    
    @Override
    public EncuestaResultadoDTO obtenerResultados(Long encuestaId, String emailUsuario) {
        Encuesta encuesta = encuestaRepository.findById(encuestaId)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

        if (!encuesta.getCreador().getEmail().equals(emailUsuario)) {
            throw new RuntimeException("No autorizado para ver resultados de esta encuesta");
        }

        // Trae todas las respuestas de la encuesta
        List<Respuesta> respuestas = respuestaRepository.findByPreguntaEncuestaId(encuestaId);

        // Agrupa las respuestas por id de pregunta
        Map<Long, List<Respuesta>> respuestasPorPregunta = respuestas.stream()
                .collect(Collectors.groupingBy(r -> r.getPregunta().getId()));

        // Construye el resultado por cada pregunta
        List<PreguntaResultadoDTO> resultados = encuesta.getPreguntas().stream()
                .map((Pregunta p) -> {
                    List<Respuesta> r = respuestasPorPregunta.getOrDefault(p.getId(), new ArrayList<>());

                    List<RespuestaEstadisticaDTO> datos;

                    if (p.getTipo() == Pregunta.Tipo.PARRAFO || p.getTipo() == Pregunta.Tipo.TEXTO_CORTO) {
                        datos = r.stream()
                                .map(res -> new RespuestaEstadisticaDTO(res.getValor(), null))
                                .toList();
                    } else {
                        datos = r.stream()
                                .collect(Collectors.groupingBy(Respuesta::getValor, Collectors.counting()))
                                .entrySet().stream()
                                .map(e -> new RespuestaEstadisticaDTO(e.getKey(), e.getValue()))
                                .toList();
                    }

                    return PreguntaResultadoDTO.builder()
                            .id(p.getId())
                            .texto(p.getTexto())
                            .tipo(p.getTipo())
                            .respuestas(datos)
                            .build();
                })
                .toList();

        return EncuestaResultadoDTO.builder()
                .encuestaId(encuesta.getId())
                .titulo(encuesta.getTitulo())
                .preguntas(resultados)
                .build();
    }

    
}
