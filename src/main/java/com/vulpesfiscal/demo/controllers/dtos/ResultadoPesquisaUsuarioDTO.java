package com.vulpesfiscal.demo.controllers.dtos;


import java.util.List;

public record ResultadoPesquisaUsuarioDTO(
        Integer id,
        Integer empresaId,
        Integer estabelecimentoId,
        String nome,
        String email,
        String username,
        String telefone,
        String senhaHash,
        Boolean ativo,
        List<String> roles
) {
}
