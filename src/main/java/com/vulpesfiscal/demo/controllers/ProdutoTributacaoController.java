package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoTributacaoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaProdutoTributacaoDTO;
import com.vulpesfiscal.demo.entities.ProdutoTributacao;
import com.vulpesfiscal.demo.services.ProdutoTributacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresa/{empresaId}/produto-tributacao")
public class ProdutoTributacaoController {

    private final ProdutoTributacaoService produtoTributacaoService;

    public ProdutoTributacaoController(ProdutoTributacaoService produtoTributacaoService) {
        this.produtoTributacaoService = produtoTributacaoService;
    }

    @PostMapping
    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO','GERENTE')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
    public ResponseEntity<Void> cadastrar(
            @PathVariable Integer empresaId,
            @RequestBody CadastroProdutoTributacaoDTO dto
    ) {
        ProdutoTributacao tributacao = produtoTributacaoService.cadastrar(dto, empresaId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO','GERENTE')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer empresaId,
            @PathVariable Long id,
            @RequestBody CadastroProdutoTributacaoDTO dto
    ) {
        ProdutoTributacao tributacao = produtoTributacaoService.atualizar(id, dto, empresaId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO','GERENTE')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
    @GetMapping("/produto/{idProduto}/uf/{uf}")
    public ResponseEntity<ResultadoPesquisaProdutoTributacaoDTO> buscar(
            @PathVariable Integer empresaId,
            @PathVariable Integer idProduto,
            @PathVariable String uf
    ) {
        ProdutoTributacao tributacao = produtoTributacaoService.buscarPorProdutoEUf(
                empresaId,
                idProduto,
                uf
        );

        return ResponseEntity.ok(ResultadoPesquisaProdutoTributacaoDTO.fromEntity(tributacao));
    }

    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO','GERENTE')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(
            @PathVariable Integer empresaId,
            @PathVariable Long id
    ) {
        produtoTributacaoService.deletar(id, empresaId);
    }

}
