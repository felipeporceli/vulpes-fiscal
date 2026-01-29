package com.vulpesfiscal.demo.controllers;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

// Implementação de uma interface genérica de controller para implementar os métodos abaixos.
public interface ControllerGenerico {

    // Metodo para gerar a URL com o id do objeto criado no header da response.
    default URI gerarHeaderLocation(Integer id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
