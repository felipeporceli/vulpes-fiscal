package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = false)
public record AtualizacaoProdutoDTO(
        @NotNull Integer id,
        Integer cfop,
        @NotNull boolean ativo,
        @NotNull Integer ncm,
        @NotBlank String descricao,
        @NotNull BigDecimal preco,
        @NotNull Integer qtdEstoque,
        @NotNull String unidade
){
}
