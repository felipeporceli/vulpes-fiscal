package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.controllers.mappers.ProdutoMapper;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.services.ProdutoService;
import com.vulpesfiscal.demo.validator.ProdutoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController implements ControllerGenerico{

    private final ProdutoService service;
    private final ProdutoMapper mapper;
    private final ProdutoValidator validator;

    // Salvar novo produto. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PostMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<Void> salvar (@RequestBody @Valid CadastroProdutoDTO dto,
                                        @PathVariable Integer empresaId,
                                        @PathVariable Integer estabelecimentoId) {
        Produto produto = service.salvar(dto, empresaId, estabelecimentoId);
        var url = gerarHeaderLocation(produto.getIdProduto());
        return ResponseEntity.created(url).build();
    }

    /* Obter detalhes por ID obtendo filtros opcionais pela URL, busca produtos paginados no banco e devolve o
    resultado convertido para DTO. */
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
    @DeleteMapping("/empresa/{empresaId}/{idProduto}")
    public void deletar (@PathVariable("idProduto") Integer idProduto, @PathVariable("empresaId") Integer empresaId) {
        service.deletar(empresaId, idProduto);
    }

    /* Atualizar produto por id na URL, mas novos atributos no body da requisicao. */
    @PutMapping("/empresa/{empresaId}/{idProduto}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer idProduto,
            @PathVariable Integer empresaId,
            @RequestBody AtualizacaoProdutoDTO dto
    ) {
        Produto produto = validator.pesquisarPorEmpresaEIdProduto(empresaId, idProduto);

        Produto dadosAtualizados = mapper.toEntityUpdate(dto, produto);

        produto.setCfop(dadosAtualizados.getCfop());
        produto.setAtivo(dadosAtualizados.isAtivo());
        produto.setNcm(dadosAtualizados.getNcm());
        produto.setDescricao(dadosAtualizados.getDescricao());
        produto.setCodigoBarras(dadosAtualizados.getCodigoBarras());
        produto.setPreco(dadosAtualizados.getPreco());
        produto.setQtdEstoque(dadosAtualizados.getQtdEstoque());
        produto.setUnidade(dadosAtualizados.getUnidade());

        service.atualizar(empresaId, idProduto, dto);

        return ResponseEntity.noContent().build();
    }


}
