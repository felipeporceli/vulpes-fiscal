package com.vulpesfiscal.demo.controllers.dtos;

public record ResultadoPesquisaConsumidorDTO(Integer id,
                                             String cpf,
                                             String nome,
                                             String email
) {
}
