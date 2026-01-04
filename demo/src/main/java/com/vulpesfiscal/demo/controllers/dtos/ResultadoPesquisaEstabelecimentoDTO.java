package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;

public record ResultadoPesquisaEstabelecimentoDTO(String cnpj,
                                                  String nomeFantasia,
                                                  String cidade,
                                                  String estado,
                                                  StatusEmpresa status,
                                                  Boolean matriz) {
}
