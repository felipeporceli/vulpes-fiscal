package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.controllers.mappers.ProdutoMapper;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.services.ProdutoService;
import com.vulpesfiscal.demo.validator.ProdutoValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos")
public class ProdutoController implements ControllerGenerico{

    private final ProdutoService service;
    private final ProdutoMapper mapper;
    private final ProdutoValidator validator;

    // Salvar novo produto. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Salvar produto", description = "Salvar produto de uma empresa no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso."),
            @ApiResponse(responseCode = "422", description = "Produto ja cadastrado com o codigo de barras."),
            @ApiResponse(responseCode = "409", description = "Produto ja cadastrado com o id na empresa."),
            @ApiResponse(responseCode = "422", description = "Campo obrigatorio nao informado."),
    })
    @PostMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<Void> salvar (@RequestBody @Valid CadastroProdutoDTO dto,
                                        @PathVariable Integer empresaId,
                                        @PathVariable Integer estabelecimentoId) {
        Produto produto = mapper.toEntity(dto);
        service.salvar(empresaId, estabelecimentoId, produto);
        var url = gerarHeaderLocation(produto.getIdProduto());
        return ResponseEntity.created(url).build();
    }

    /* Obter detalhes por ID obtendo filtros opcionais pela URL, busca produtos paginados no banco e devolve o
    resultado convertido para DTO. */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Obter informacoes do produto", description = "Obter informacoes do produto por filtros.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Empresa nao encontrada."),
            @ApiResponse(responseCode = "403", description = "Usuario não possui permissao para acessar este recurso."),
            @ApiResponse(responseCode = "422", description = "Campo obrigatorio nao informado."),
    })
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<ResultadoPesquisaProdutoDTO>> pesquisa (
            @RequestParam (value = "descricao", required = false)
            String descricao,

            @RequestParam (value = "codigo-de-barras", required = false)
            String codigoBarras,

            @RequestParam (value = "ncm", required = false)
            Integer ncm,

            @RequestParam (value = "id-produto", required = false)
            Integer idProduto,

            @RequestParam (value = "preco-min", required = false)
            BigDecimal precoMin,

            @RequestParam (value = "preco-max", required = false)
            BigDecimal precoMax,

            @RequestParam (value = "ativo", required = false, defaultValue = "true")
            boolean ativo,

            @RequestParam (value = "pagina", defaultValue = "0")
            Integer pagina,

            @RequestParam (value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina,

            @PathVariable Integer empresaId
    ) {
        Page<Produto> paginaResultado = service.pesquisar(empresaId,idProduto, descricao, codigoBarras, ncm, precoMin, precoMax, ativo, pagina, tamanhoPagina);
        Page<ResultadoPesquisaProdutoDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }




    /* Deletar produto por id na URL, .map para seguir práticas Rest */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Deletar produto", description = "Deletar produto do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Produto ou Empresa nao encontrado(s)."),
            @ApiResponse(responseCode = "403", description = "Usuario não possui permissao para acessar este recurso.")
    })
    @DeleteMapping("/empresa/{empresaId}/{idProduto}")
    public void deletar (@PathVariable("idProduto") Integer idProduto,
                         @PathVariable("empresaId") Integer empresaId) {
        service.deletar(empresaId, idProduto);
    }


    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO','GERENTE','CAIXA')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
    @Operation(summary = "Atualizar produto", description = "Atualizar produto do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Produto ou Empresa nao encontrado(s)."),
            @ApiResponse(responseCode = "403", description = "Usuario não possui permissao para acessar este recurso.")
    })
    @PutMapping("/empresa/{empresaId}/{idProduto}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer idProduto,
            @PathVariable Integer empresaId,
            @RequestBody AtualizacaoProdutoDTO dto
    ) {
        service.atualizar(empresaId, idProduto, dto);
        return ResponseEntity.noContent().build();
    }


}
