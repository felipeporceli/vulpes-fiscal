package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.validator.annotation.CpfOuCnpjExclusivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

@CpfOuCnpjExclusivo
public record CadastroConsumidorDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @CPF
        String cpf,

        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @CNPJ
        String cnpj,

        String estrangeiroId,

        String inscricaoEstadual,

        String indicadorInscricao,

        String inscricaoSuframa,

        String inscricaoMunicipal,

        String logradouro,

        String numero,

        String complemento,

        String bairro,

        String municipioId,

        String municipio,

        String uf,

        String cep,

        String paisId,

        String pais,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone

) {}
