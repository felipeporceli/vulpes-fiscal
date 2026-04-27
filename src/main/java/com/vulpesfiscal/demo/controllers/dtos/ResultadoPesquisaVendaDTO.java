package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResultadoPesquisaVendaDTO(
        Integer id,
        Integer empresaId,
        Integer estabelecimentoId,
        BigDecimal valorTotal,
        Integer parcelas,
        LocalDateTime dataCriacao,
        Integer consumidorId,
        String consumidorNome,
        MetodoPagamento metodoPagamento,
        StatusPagamento statusPagamento,
        BigDecimal valorFinal,
        BigDecimal desconto,
        Integer vendedorId,
        String vendedorNome,
        BigDecimal valorRecebido,
        String usuarioNome
) {
}
