package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.entities.Client;
import com.vulpesfiscal.demo.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("clients")
@RestController
@RequiredArgsConstructor
@Tag(name = "Clients")
public class ClientController {

    private final ClientService service;


    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Salvar clients", description = "Cadastrar novo client")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cadastrado com sucesso")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void salvar (@RequestBody Client client) {
        service.salvar(client);
    }

}
