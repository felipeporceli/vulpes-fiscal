package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.controllers.mappers.PagamentoMapper;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import com.vulpesfiscal.demo.exceptions.PagamentoNaoEncontradoException;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.services.PagamentoService;
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
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos")
public class PagamentoController implements ControllerGenerico{

    private final PagamentoService service;
    private final PagamentoMapper mapper;


    // Salvar novo Pagamento. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Criar pagamento", description = "Registrar pagamento no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pagamento registrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Estabelecimento(s) ou Empresa(s) nao encontrado(s)"),
            @ApiResponse(responseCode = "422", description = "Campo obrigatorio nao informado."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),

    })
    @PostMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<Void> salvar (
            @RequestBody @Valid CadastroPagamentoDTO dto,
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId) {
        Pagamento pagamento = mapper.toEntity(dto);
        service.salvar(empresaId, estabelecimentoId, dto);
        var url = gerarHeaderLocation(pagamento.getId());
        return ResponseEntity.created(url).build();
    }



    /* Obter detalhes por ID obtendo filtros opcionais pela URL, busca produtos paginados no banco e devolve o
    resultado convertido para DTO. */
    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Obter informacoes do pagamento", description = "Obter informacoes do produto por filtros.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pagamento registrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Estabelecimento(s) ou Empresa(s) nao encontrado(s)"),
            @ApiResponse(responseCode = "422", description = "Campo obrigatorio nao informado."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),

    })
    @GetMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<Page<ResultadoPesquisaPagamentoDTO>> pesquisa (
            @RequestParam (value = "id", required = false)
            Integer id,

            @RequestParam (value = "metodo-pagamento", required = false)
            MetodoPagamento metodoPagamento,

            @RequestParam (value = "status-pagamento", required = false)
            StatusPagamento statusPagamento,

            @RequestParam (value = "valor-min", required = false)
            BigDecimal valorMin,

            @RequestParam (value = "valor-max", required = false)
            BigDecimal valorMax,

            @RequestParam (value = "pagina", defaultValue = "0")
            Integer pagina,

            @RequestParam (value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina,

            @PathVariable Integer empresaId,

            @PathVariable Integer estabelecimentoId
    ) {
        Page<Pagamento> paginaResultado = service.pesquisar(id, metodoPagamento, valorMin, valorMax, statusPagamento, empresaId, estabelecimentoId, pagina, tamanhoPagina);
        Page<ResultadoPesquisaPagamentoDTO> resultado = paginaResultado.map(mapper::toDTO);
        return ResponseEntity.ok(resultado);
    }


    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )

    @Operation(summary = "Deletar pagamento do sistema.", description = "Deletar pagamento do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Pagamento nao encontrado."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),

    })


    @DeleteMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}/{pagamentoId}")
    public void deletar (@PathVariable("empresaId") Integer empresaId,
                         @PathVariable("estabelecimentoId") Integer estabelecimentoId,
                         @PathVariable("pagamentoId") Integer pagamentoId) {

        service.deletarPagamento(pagamentoId, empresaId, estabelecimentoId);
    }


    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "T(java.util.Objects).equals(#empresaId, principal.claims['empresaId']))"
    )

    @Operation(summary = "Atualizar pagamento.", description = "Atualizar pagamento do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pagamento atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Pagamento nao encontrado."),
            @ApiResponse(responseCode = "422", description = "Valor StatusPagamento invalido."),

    })
    @PutMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}/{pagamentoId}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId,
            @PathVariable Integer pagamentoId,
            @RequestBody AtualizacaoPagamentoDTO dto
    ) {

        service.atualizarPagamento(pagamentoId, empresaId, estabelecimentoId, dto);

        return ResponseEntity.noContent().build();
    }



}
