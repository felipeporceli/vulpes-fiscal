package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.br.CNPJ;

import java.time.LocalDate;

public record CadastroEstabelecimentoDTO(

        String nomeFantasia,

        @NotBlank(message = "Campo obrigatório")
        @CNPJ
        String cnpj,

        @NotBlank(message = "Campo obrigatório")
        String inscricaoEstadual,

        @NotBlank(message = "Campo obrigatório")
        String endereco,

        @NotBlank(message = "Campo obrigatório")
        String cidade,

        @NotBlank(message = "Campo obrigatório")
        String estado,

        @NotNull(message = "Campo obrigatório")
        StatusEmpresa status,

        @NotNull(message = "Campo obrigatório")
        String inscricaoMunicipal,

        @NotNull(message = "Campo obrigatório")
        boolean matriz,

        String telefone,

        String email,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @NotNull
        @Past(message = "Não pode ser data futura")
        LocalDate dataAbertura
){
}
