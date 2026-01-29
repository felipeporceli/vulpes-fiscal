package com.vulpesfiscal.demo.controllers.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendaResponseDTO (

         Integer id,

         LocalDateTime dataCriacao,

         Integer criadoPor,

         LocalDateTime atualizadoEm,

         Integer atualizadoPor,

         BigDecimal desconto,

         Integer empresaId,

         ConsumidorResponseDTO consumidor
) {
}
