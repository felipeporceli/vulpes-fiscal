package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vulpesfiscal.demo.entities.enums.AmbienteSefazEmpresa;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = false)
public record AtualizacaoEmpresaDTO(
        @NotBlank String razaoSocial,
        String nomeFantasia,
        @NotBlank String inscricaoEstadual,
        RegimeTributarioEmpresa regimeTributario,
        PorteEmpresa porte,
        AmbienteSefazEmpresa ambienteSefaz,
        StatusEmpresa status,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataAbertura
){
}

