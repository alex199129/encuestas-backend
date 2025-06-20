package com.davivienda.encuestas.controller;

import com.davivienda.encuestas.dto.EncuestaPublicaResponse;
import com.davivienda.encuestas.dto.RespuestasEncuestaRequest;
import com.davivienda.encuestas.service.EncuestaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/encuestas")
@RequiredArgsConstructor
public class EncuestaPublicaController {

    private final EncuestaService encuestaService;

    @GetMapping("/{slug}")
    public ResponseEntity<EncuestaPublicaResponse> obtenerEncuesta(@PathVariable String slug) {
        return ResponseEntity.ok(encuestaService.obtenerEncuestaPublica(slug));
    }

    @PostMapping("/{slug}/responder")
    public ResponseEntity<Void> responderEncuesta(@PathVariable String slug,
                                                  @RequestBody RespuestasEncuestaRequest request) {
        encuestaService.guardarRespuestas(slug, request.getRespuestas());
        return ResponseEntity.ok().build();
    }
    
}
