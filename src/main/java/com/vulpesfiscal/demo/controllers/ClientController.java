package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.entities.Client;
import com.vulpesfiscal.demo.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("clients")
@RestController
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;


    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void salvar (@RequestBody Client client) {
        service.salvar(client);
    }

}
