package com.davivienda.encuestas.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpcionDTO {
    private Long id;
    private String texto;
}
