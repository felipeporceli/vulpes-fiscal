package com.vulpesfiscal.demo.controllers.dtos;

import java.util.List;

public record CadastroNfceDTO (
        Integer serie,
        List<CadastroItemNfceDTO> itens
) {
}
