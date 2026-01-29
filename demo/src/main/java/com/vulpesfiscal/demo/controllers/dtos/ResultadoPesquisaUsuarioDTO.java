package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record ResultadoPesquisaUsuarioDTO(
        Integer perfilId,
        Integer empresaId,
        Integer estabelecimentoId,
        String nome,
        String email,
        String senhaHash,
        Boolean ativo
) {
}
