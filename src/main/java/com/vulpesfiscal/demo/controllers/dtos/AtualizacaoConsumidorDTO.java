package com.vulpesfiscal.demo.controllers.dtos;

import jakarta.validation.constraints.Email;

public record AtualizacaoConsumidorDTO(

        String nome,

        @Email(message = "E-mail em formato inválido")
        String email,

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

        String telefone

) {
}

