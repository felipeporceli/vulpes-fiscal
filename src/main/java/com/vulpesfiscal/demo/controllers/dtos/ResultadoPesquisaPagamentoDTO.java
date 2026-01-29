package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record ResultadoPesquisaPagamentoDTO(MetodoPagamento metodoPagamento,
                                            BigDecimal valor,
                                            BigDecimal troco,
                                            Integer parcelas,
                                            StatusPagamento statusPagamento,
                                            Integer empresaId,
                                            Integer estabelecimentoId,
                                            Timestamp pagoEm
) {
}
