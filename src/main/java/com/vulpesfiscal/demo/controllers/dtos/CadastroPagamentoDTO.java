package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vulpesfiscal.demo.entities.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CadastroPagamentoDTO(

        @NotNull(message = "Campo obrigatório")
        MetodoPagamento metodoPagamento,

        @NotNull(message = "Campo obrigatório")
        BigDecimal valor,

        @NotNull(message = "Campo obrigatório")
        BigDecimal troco,

        Integer parcelas,

        @NotNull(message = "Campo obrigatório")
        StatusPagamento statusPagamento
){
}
