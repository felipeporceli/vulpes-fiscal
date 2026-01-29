package com.vulpesfiscal.demo.controllers.dtos;

import java.math.BigDecimal;

public record ResultadoPesquisaProdutoDTO(String descricao,
                                          String codigoBarras,
                                          Integer idProduto,
                                          Integer ncm,
                                          Integer cfop ,
                                          String unidade,
                                          BigDecimal preco,
                                          boolean ativo,
                                          Integer qtdEstoque
) {
}
