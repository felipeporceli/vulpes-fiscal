package com.vulpesfiscal.demo.controllers.dtos;


public record ResultadoPesquisaUsuarioDTO(
        Integer id,
        Integer perfilId,
        Integer empresaId,
        Integer estabelecimentoId,
        String nome,
        String email,
        String username,
        String telefone,
        String senhaHash,
        Boolean ativo
) {
}
