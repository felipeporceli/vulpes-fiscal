package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record ResultadoPesquisaPagamentoDTO(
        MetodoPagamento metodoPagamento,
        BigDecimal valor,
        BigDecimal valorRecebido,
        BigDecimal troco,
        BigDecimal desconto,
        BigDecimal valorFinal,
        Integer parcelas,
        StatusPagamento statusPagamento,
        Integer vendaId,
        Integer empresaId,
        Integer estabelecimentoId,
        Integer consumidorId,
        LocalDateTime dataCriacao,
        LocalDateTime atualizadoEm
) {}
