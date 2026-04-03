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
public class PagamentoController implements ControllerGenerico{

    private final PagamentoService service;
    private final PagamentoMapper mapper;


    // Salvar novo Pagamento. Finalizando gerando a URL da nova entidade e entregando-a no header da response.
    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
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
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
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
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
    @DeleteMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}/{pagamentoId}")
    public void deletar (@PathVariable("empresaId") Integer empresaId,
                         @PathVariable("estabelecimentoId") Integer estabelecimentoId,
                         @PathVariable("pagamentoId") Integer pagamentoId) {

        service.deletarPagamento(pagamentoId, empresaId, estabelecimentoId);
    }


    @PreAuthorize(
            "(hasAnyRole('ADMINISTRADOR','SUPORTE')) or " +
                    "((hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR')) and " +
                    "(#empresaId == authentication.principal.empresaId))"
    )
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
