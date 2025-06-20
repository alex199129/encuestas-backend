package com.davivienda.encuestas.service.impl;

import com.davivienda.encuestas.dto.CrearEncuestaRequest;
import com.davivienda.encuestas.dto.PreguntaDTO;
import com.davivienda.encuestas.dto.OpcionDTO;
import com.davivienda.encuestas.model.Encuesta;
import com.davivienda.encuestas.model.Pregunta;
import com.davivienda.encuestas.model.Usuario;
import com.davivienda.encuestas.repository.EncuestaRepository;
import com.davivienda.encuestas.repository.PreguntaRepository;
import com.davivienda.encuestas.repository.RespuestaRepository;
import com.davivienda.encuestas.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EncuestaServiceImplTest {

    private EncuestaRepository encuestaRepository;
    private UsuarioRepository usuarioRepository;
    private PreguntaRepository preguntaRepository;
    private RespuestaRepository respuestaRepository;

    private EncuestaServiceImpl encuestaService;

    @BeforeEach
    void setUp() {
        encuestaRepository = mock(EncuestaRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        preguntaRepository = mock(PreguntaRepository.class);
        respuestaRepository = mock(RespuestaRepository.class);

        encuestaService = new EncuestaServiceImpl(
            encuestaRepository, usuarioRepository, preguntaRepository, respuestaRepository
        );
    }

    @Test
    void testCrearEncuesta_exito() {
        // Arrange
        String email = "usuario@correo.com";

        CrearEncuestaRequest request = new CrearEncuestaRequest();
        request.setTitulo("Encuesta de prueba");
        request.setDescripcion("Descripción de prueba");

        PreguntaDTO preguntaDTO = new PreguntaDTO();
        preguntaDTO.setTexto("¿Cuál es tu color favorito?");
        preguntaDTO.setTipo(Pregunta.Tipo.SELECCION_UNICA);

        OpcionDTO opcionDTO = new OpcionDTO();
        opcionDTO.setTexto("Rojo");
        preguntaDTO.setOpciones(List.of(opcionDTO));
        request.setPreguntas(List.of(preguntaDTO));

        Usuario usuario = Usuario.builder().email(email).build();
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Encuesta encuestaGuardada = Encuesta.builder().id(1L).titulo("Encuesta de prueba").build();
        when(encuestaRepository.save(any(Encuesta.class))).thenReturn(encuestaGuardada);

        // Act
        var response = encuestaService.crearEncuesta(request, email);

        // Assert
        assertNotNull(response);
        assertEquals("Encuesta de prueba", response.getTitulo());
        verify(encuestaRepository, times(1)).save(any(Encuesta.class));
    }
    

    @Test
    void testEliminarEncuesta_encuestaNoEncontrada() {
        Long id = 1L;
        when(encuestaRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            encuestaService.eliminarEncuesta(id, "usuario@correo.com"));

        assertEquals("Encuesta no encontrada", ex.getMessage());
    }
    
    @Test
    void testEliminarEncuesta_usuarioNoAutorizado() {
        Long id = 1L;
        Usuario otroUsuario = Usuario.builder().email("otro@correo.com").build();
        Encuesta encuesta = Encuesta.builder().id(id).creador(otroUsuario).build();

        when(encuestaRepository.findById(id)).thenReturn(Optional.of(encuesta));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            encuestaService.eliminarEncuesta(id, "usuario@correo.com"));

        assertEquals("No autorizado para eliminar esta encuesta", ex.getMessage());
    }
    
    
}
