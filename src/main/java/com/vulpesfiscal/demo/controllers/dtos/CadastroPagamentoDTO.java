package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vulpesfiscal.demo.entities.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;

import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CadastroPagamentoDTO(

        MetodoPagamento metodoPagamento,
        BigDecimal valor,
        @PositiveOrZero(message = "Valor recebido não pode ser negativo")
        BigDecimal valorRecebido,
        StatusPagamento statusPagamento,
        @PositiveOrZero(message = "Desconto não pode ser negativo")
        BigDecimal desconto,
        Integer consumidorId,
        @PositiveOrZero(message = "Parcelas não pode ser negativo")
        Integer parcelas
) {}
