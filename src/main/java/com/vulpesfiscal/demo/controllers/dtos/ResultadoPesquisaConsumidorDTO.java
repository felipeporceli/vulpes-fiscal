package com.vulpesfiscal.demo.controllers.dtos;
public record ResultadoPesquisaConsumidorDTO(

        Integer id,
        String nome,
        String cpf,
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

) {}
