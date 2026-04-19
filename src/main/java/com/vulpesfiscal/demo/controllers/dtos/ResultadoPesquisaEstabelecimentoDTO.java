package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;

import java.time.LocalDate;

public record ResultadoPesquisaEstabelecimentoDTO(
        Integer id,
        String nomeFantasia,
        String cnpj,
        String telefone,
        String email,
        String inscricaoEstadual,
        String inscricaoMunicipal,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String municipioId,
        String paisId,
        String pais,
        String codUf,
        StatusEmpresa status,
        boolean matriz,
        LocalDate dataAbertura,
        LocalDate dataFechamento
) {}
