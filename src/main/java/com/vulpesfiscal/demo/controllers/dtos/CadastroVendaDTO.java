package com.vulpesfiscal.demo.controllers.dtos;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public record CadastroVendaDTO(
        Integer consumidorId,
        Integer vendedorId,
        @Valid
        List<CadastroItemVendaDTO> itens,
        @Valid
        CadastroPagamentoDTO pagamento,
        Boolean emitirNfce,
        BigDecimal desconto
) {
}
