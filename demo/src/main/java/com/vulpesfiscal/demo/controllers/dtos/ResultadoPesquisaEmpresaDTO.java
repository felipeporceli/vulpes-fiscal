package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;

public record ResultadoPesquisaEmpresaDTO(String cnpj,
                                          String razaoSocial,
                                          String inscricaoEstadual,
                                          String regimeTributario,
                                          StatusEmpresa statusEmpresa) {
}
