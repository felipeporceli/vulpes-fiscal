package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vulpesfiscal.demo.entities.enums.AmbienteSefazEmpresa;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = false)
public record AtualizacaoEmpresaDTO(
        String razaoSocial,
        String nomeFantasia,
        String inscricaoEstadual,
        RegimeTributarioEmpresa regimeTributario,
        PorteEmpresa porte,
        AmbienteSefazEmpresa ambienteSefaz,
        StatusEmpresa status,
        @PastOrPresent(message = "Data de abertura não pode ser uma data futura")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataAbertura,
        @JsonFormat(pattern = "dd/MM/yyyy")
        String cnae,
        String uf
){
}

