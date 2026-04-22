package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaProdutoTributacaoDTO;
import com.vulpesfiscal.demo.services.ProdutoTributacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produto-tributacao")
@RequiredArgsConstructor
public class ProdutoTributacaoAdminController {

    private final ProdutoTributacaoService service;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPORTE')")
    @GetMapping
    public ResponseEntity<List<ResultadoPesquisaProdutoTributacaoDTO>> listarTodas() {
        List<ResultadoPesquisaProdutoTributacaoDTO> result = service.listarTodas()
                .stream().map(ResultadoPesquisaProdutoTributacaoDTO::fromEntity).toList();
        return ResponseEntity.ok(result);
    }
}
