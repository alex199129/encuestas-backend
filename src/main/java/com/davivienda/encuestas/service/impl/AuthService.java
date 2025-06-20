package com.davivienda.encuestas.service.impl;

import com.davivienda.encuestas.dto.AuthResponse;
import com.davivienda.encuestas.dto.LoginRequest;
import com.davivienda.encuestas.dto.RegisterRequest;
import com.davivienda.encuestas.model.Usuario;
import com.davivienda.encuestas.repository.UsuarioRepository;
import com.davivienda.encuestas.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponse register(RegisterRequest request) {
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .build();
        usuarioRepository.save(usuario);
        String jwt = jwtService.generateToken(usuario.getEmail());
        return new AuthResponse(jwt, usuario.getNombre());
    }

    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow();
        String jwt = jwtService.generateToken(usuario.getEmail());
        return new AuthResponse(jwt, usuario.getNombre());
    }
}
