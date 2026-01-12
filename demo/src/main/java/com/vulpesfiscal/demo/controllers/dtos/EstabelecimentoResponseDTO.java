package com.vulpesfiscal.demo.controllers.dtos;

public record EstabelecimentoResponseDTO(
        Integer id,
        String cnpj,
        String cidade,
        String email
) {
}
