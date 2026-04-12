package com.vulpesfiscal.demo.controllers.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CadastroItemVendaDTO(

        @NotNull(message = "idProduto é obrigatório")
        Integer idProduto,

        @NotNull(message = "quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade não pode ser zero ou negativa")
        Integer quantidade,

        Integer cfop
) {}
