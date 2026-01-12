package com.vulpesfiscal.demo.controllers.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CadastroItemNfceDTO(
        @NotNull
        Integer produtoId,
        @NotNull
        @Positive
        BigDecimal quantidade){
}
