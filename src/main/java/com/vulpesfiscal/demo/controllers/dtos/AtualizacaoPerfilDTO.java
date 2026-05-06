package com.vulpesfiscal.demo.controllers.dtos;

public record AtualizacaoPerfilDTO(
        String nome,
        String email,
        String telefone,
        String senha
) {}
