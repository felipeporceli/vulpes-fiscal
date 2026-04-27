package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoTributacaoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaProdutoTributacaoDTO;
import com.vulpesfiscal.demo.controllers.mappers.ProdutoTributacaoMapper;
import com.vulpesfiscal.demo.entities.ProdutoTributacao;
import com.vulpesfiscal.demo.services.ProdutoTributacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresa/{empresaId}/produto-tributacao")
@RequiredArgsConstructor
@Tag(name = "Produtos Tributacao")
public class ProdutoTributacaoController implements ControllerGenerico {

    private final ProdutoTributacaoService service;
    private final ProdutoTributacaoMapper mapper;


    // Salvar nova tributação. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Cadastrar novo Produto Tributacao no sistema.",
            description = "Cadastrar novo Produto Tributacao no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto Tributacao cadastrado com sucesso."),
            @ApiResponse(responseCode = "409", description = "Ja existe tributacao cadastrada para o produto na UF."),
            @ApiResponse(responseCode = "404", description = "Empresa nao encontrada."),
            @ApiResponse(responseCode = "422", description = "Campo invalido ou obrigatorio."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @PostMapping
    public ResponseEntity<Void> salvar(
            @PathVariable Integer empresaId,
            @RequestBody @Valid CadastroProdutoTributacaoDTO dto
    ) {
        ProdutoTributacao tributacao = service.salvar(dto, empresaId);
        var url = gerarHeaderLocation(tributacao.getId());
        return ResponseEntity.created(url).build();
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Listar todas as tributações da empresa.", description = "Retorna todas as tributações cadastradas para a empresa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para esse recurso."),
    })
    @GetMapping
    public ResponseEntity<List<ResultadoPesquisaProdutoTributacaoDTO>> listarPorEmpresa(
            @PathVariable Integer empresaId
    ) {
        List<ResultadoPesquisaProdutoTributacaoDTO> result = service.listarPorEmpresa(empresaId)
                .stream().map(ResultadoPesquisaProdutoTributacaoDTO::fromEntity).toList();
        return ResponseEntity.ok(result);
    }

    /* Obter detalhes da tributação por produto e UF. */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Obter tributacao do Produto.", description = "Obter tributacao do Produto por UF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Tributacao nao encontrada."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @GetMapping("/produto/{idProduto}/uf/{uf}")
    public ResponseEntity<ResultadoPesquisaProdutoTributacaoDTO> buscar(
            @PathVariable Integer empresaId,
            @PathVariable Integer idProduto,
            @PathVariable String uf
    ) {
        ProdutoTributacao tributacao = service.buscarPorProdutoEUf(empresaId, idProduto, uf);
        return ResponseEntity.ok(ResultadoPesquisaProdutoTributacaoDTO.fromEntity(tributacao));
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Listar tributações de um produto.", description = "Retorna todas as tributações cadastradas para um produto da empresa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado."),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para esse recurso."),
    })
    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<List<ResultadoPesquisaProdutoTributacaoDTO>> listarPorProduto(
            @PathVariable Integer empresaId,
            @PathVariable Integer idProduto
    ) {
        List<ResultadoPesquisaProdutoTributacaoDTO> result = service.listarPorProduto(empresaId, idProduto)
                .stream().map(ResultadoPesquisaProdutoTributacaoDTO::fromEntity).toList();
        return ResponseEntity.ok(result);
    }

    /* Deletar tributação por id na URL. */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Deletar Produto Tributacao do sistema.", description = "Deletar Produto Tributacao do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tributacao deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Tributacao nao encontrada."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @DeleteMapping("/{id}")
    public void deletar(
            @PathVariable Integer empresaId,
            @PathVariable Integer id
    ) {
        service.deletar(id, empresaId);
    }

    /* Atualizar tributação por id na URL, com novos atributos no body da requisição. */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Atualizar Produto Tributacao no sistema.", description = "Atualizar Produto Tributacao do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tributacao atualizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Tributacao nao encontrada."),
            @ApiResponse(responseCode = "422", description = "Campo invalido ou obrigatorio."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer empresaId,
            @PathVariable Integer id,
            @RequestBody @Valid CadastroProdutoTributacaoDTO dto
    ) {
        service.atualizar(id, dto, empresaId);
        return ResponseEntity.noContent().build();
    }
}