package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CadastroUsuarioDTO(

        @NotNull(message = "Campo obrigatório")
        Integer perfilId,

        @NotBlank(message = "Campo obrigatório")
        String nome,

        @NotBlank(message = "Campo obrigatório")
        String email,

        @NotBlank(message = "Campo obrigatório")
        String senha,

        @NotNull(message = "Campo obrigatório")
        Boolean ativo
){
}
