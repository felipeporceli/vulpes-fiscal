package com.vulpesfiscal.demo.controllers.dtos;

import java.math.BigDecimal;

public record ResultadoPesquisaProdutoDTO(
        Integer empresaId,
        Integer idProduto,
        String descricao,
        String codigoBarras,
        Integer ncm,
        Integer cfop,
        String unidade,
        BigDecimal preco,
        boolean ativo,
        Integer qtdEstoque,
        String cest,
        Integer orig
) {
}
