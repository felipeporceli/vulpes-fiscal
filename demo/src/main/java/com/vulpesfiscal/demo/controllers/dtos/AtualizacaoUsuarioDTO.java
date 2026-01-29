package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vulpesfiscal.demo.entities.enums.AmbienteSefazEmpresa;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = false)
public record AtualizacaoUsuarioDTO(
        @NotBlank String nome,
        @NotBlank String email,
        @NotBlank String senha,
        @NotNull Boolean ativo,
        @NotNull Integer estabelecimentoId,
        @NotNull Integer perfilId
){
}

