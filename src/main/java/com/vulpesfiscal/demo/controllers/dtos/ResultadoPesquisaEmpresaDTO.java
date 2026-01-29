package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;

import java.util.List;

public record ResultadoPesquisaEmpresaDTO(String cnpj,
                                          String razaoSocial,
                                          String inscricaoEstadual,
                                          RegimeTributarioEmpresa regimeTributario,
                                          StatusEmpresa status,
                                          PorteEmpresa porte,
                                          List<ResultadoPesquisaEstabelecimentoDTO> estabelecimentos
) {
}
