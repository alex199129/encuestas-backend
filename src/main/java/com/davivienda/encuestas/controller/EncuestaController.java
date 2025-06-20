package com.davivienda.encuestas.controller;

import com.davivienda.encuestas.dto.CrearEncuestaRequest;
import com.davivienda.encuestas.dto.EncuestaResponse;
import com.davivienda.encuestas.dto.EncuestaResultadoDTO;
import com.davivienda.encuestas.service.EncuestaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encuestas")
@RequiredArgsConstructor
public class EncuestaController {

    private final EncuestaService encuestaService;

    @PostMapping
    public ResponseEntity<EncuestaResponse> crearEncuesta(@RequestBody CrearEncuestaRequest request,
                                                          @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(encuestaService.crearEncuesta(request, user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<EncuestaResponse>> listar(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(encuestaService.listarEncuestasUsuario(user.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncuestaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(encuestaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EncuestaResponse> actualizar(@PathVariable Long id,
                                                       @RequestBody CrearEncuestaRequest request,
                                                       @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(encuestaService.actualizarEncuesta(id, request, user.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails user) {
        encuestaService.eliminarEncuesta(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/tiene-respuestas")
    public ResponseEntity<Boolean> tieneRespuestas(@PathVariable Long id) {
        return ResponseEntity.ok(encuestaService.encuestaTieneRespuestas(id));
    }
    
    @GetMapping("/{id}/resultados")
    public ResponseEntity<EncuestaResultadoDTO> obtenerResultados(@PathVariable Long id,
                                                                  @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(encuestaService.obtenerResultados(id, user.getUsername()));
    }
    
}
