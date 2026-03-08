package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoTributacaoDTO;
import com.vulpesfiscal.demo.entities.ProdutoTributacao;
import com.vulpesfiscal.demo.services.ProdutoTributacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresa/{empresaId}/produto-tributacao")
public class ProdutoTributacaoController {

    private final ProdutoTributacaoService produtoTributacaoService;

    public ProdutoTributacaoController(ProdutoTributacaoService produtoTributacaoService) {
        this.produtoTributacaoService = produtoTributacaoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoTributacao> cadastrar(
            @PathVariable Integer empresaId,
            @RequestBody CadastroProdutoTributacaoDTO dto
    ) {
        ProdutoTributacao tributacao = produtoTributacaoService.cadastrar(dto, empresaId);
        return ResponseEntity.ok(tributacao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoTributacao> atualizar(
            @PathVariable Integer empresaId,
            @PathVariable Long id,
            @RequestBody CadastroProdutoTributacaoDTO dto
    ) {
        ProdutoTributacao tributacao = produtoTributacaoService.atualizar(id, dto, empresaId);
        return ResponseEntity.ok(tributacao);
    }

    @GetMapping("/produto/{idProduto}/uf/{uf}")
    public ResponseEntity<ProdutoTributacao> buscarPorProdutoEUf(
            @PathVariable Integer empresaId,
            @PathVariable Integer idProduto,
            @PathVariable String uf
    ) {
        ProdutoTributacao tributacao = produtoTributacaoService.buscarPorProdutoEUf(empresaId, idProduto, uf);
        return ResponseEntity.ok(tributacao);
    }
}
