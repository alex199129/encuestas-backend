package com.davivienda.encuestas.dto;

import com.davivienda.encuestas.model.Usuario;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private Usuario.Rol rol; // EMPRESA o USUARIO
}
