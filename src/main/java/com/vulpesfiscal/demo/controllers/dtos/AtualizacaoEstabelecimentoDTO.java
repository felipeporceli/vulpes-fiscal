package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = false)
public record AtualizacaoEstabelecimentoDTO(

        String nomeFantasia,

        String inscricaoEstadual,

        String logradouro,

        String numero,

        String complemento,

        String bairro,

        String municipioId,

        String cep,

        String paisId,

        String pais,

        String codUf,

        String cidade,

        String estado,

        StatusEmpresa status,

        String inscricaoMunicipal,

        Boolean matriz,

        String telefone,

        String email,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataFechamento,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @Past(message = "Não pode ser data futura")
        LocalDate dataAbertura
){
}
