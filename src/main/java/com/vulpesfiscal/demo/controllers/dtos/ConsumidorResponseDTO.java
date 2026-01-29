package com.vulpesfiscal.demo.controllers.dtos;

public record ConsumidorResponseDTO(
        Integer id,
        String nome,
        String cpf,
        String email
) {
}
