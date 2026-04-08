package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = false)
public record AtualizacaoProdutoDTO(
        String descricao,
        String codigoBarras,
        Integer qtdEstoque,
        String ncm,
        Integer cfop,
        String unidade,
        BigDecimal preco,
        Boolean ativo,
        String cest,
        Integer orig
){
}
