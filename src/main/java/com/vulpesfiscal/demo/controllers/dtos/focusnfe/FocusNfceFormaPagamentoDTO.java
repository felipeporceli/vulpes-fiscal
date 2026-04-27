package com.vulpesfiscal.demo.controllers.dtos.focusnfe;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FocusNfceFormaPagamentoDTO {

    /** Código SEFAZ: 01=Dinheiro, 03=Cartão Crédito, 04=Cartão Débito, 17=PIX, etc. */
    private String formaPagamento;
    private BigDecimal valorPagamento;
}
