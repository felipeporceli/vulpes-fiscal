package com.vulpesfiscal.demo.controllers.dtos;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public record CadastroVendaDTO(
        Integer consumidorId,
        List<CadastroItemVendaDTO> itens,
        CadastroPagamentoDTO pagamento,
        Boolean emitirNfce,
        BigDecimal desconto
) {
}
