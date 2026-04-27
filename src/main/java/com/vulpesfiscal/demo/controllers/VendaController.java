package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.VendaResponseDTO;
import com.vulpesfiscal.demo.controllers.mappers.VendaMapper;
import com.vulpesfiscal.demo.entities.Venda;
import com.vulpesfiscal.demo.services.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
@Tag(name = "Vendas")
public class VendaController {

    private final VendaService vendaService;
    private final VendaMapper vendaMapper;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPORTE')")
    @Operation(summary = "Pesquisa global de Vendas.", description = "Pesquisa vendas sem restrição de empresa (ADMINISTRADOR/SUPORTE).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @GetMapping
    public ResponseEntity<Page<ResultadoPesquisaVendaDTO>> pesquisaGlobal(
            @RequestParam(value = "empresa-id",         required = false) Integer empresaId,
            @RequestParam(value = "estabelecimento-id", required = false) Integer estabelecimentoId,
            @RequestParam(value = "consumidor-id",      required = false) Integer consumidorId,
            @RequestParam(value = "data-inicio",        required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(value = "data-fim",           required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(value = "pagina",             defaultValue = "0")  Integer pagina,
            @RequestParam(value = "tamanho-pagina",     defaultValue = "10") Integer tamanhoPagina,
            @RequestParam(value = "ordenar-por",        required = false) String ordenarPor,
            @RequestParam(value = "direcao",            required = false, defaultValue = "desc") String direcao
    ) {
        return ResponseEntity.ok(vendaService.pesquisar(
                empresaId, estabelecimentoId, consumidorId, dataInicio, dataFim,
                pagina, tamanhoPagina, ordenarPor, direcao));
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Pesquisa Vendas por empresa.", description = "Pesquisa vendas filtradas pela empresa do usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),
    })
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<ResultadoPesquisaVendaDTO>> pesquisa(
            @PathVariable Integer empresaId,
            @RequestParam(value = "estabelecimento-id", required = false) Integer estabelecimentoId,
            @RequestParam(value = "consumidor-id",      required = false) Integer consumidorId,
            @RequestParam(value = "data-inicio",        required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(value = "data-fim",           required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(value = "pagina",             defaultValue = "0")  Integer pagina,
            @RequestParam(value = "tamanho-pagina",     defaultValue = "10") Integer tamanhoPagina,
            @RequestParam(value = "ordenar-por",        required = false) String ordenarPor,
            @RequestParam(value = "direcao",            required = false, defaultValue = "desc") String direcao
    ) {
        return ResponseEntity.ok(vendaService.pesquisar(
                empresaId, estabelecimentoId, consumidorId, dataInicio, dataFim,
                pagina, tamanhoPagina, ordenarPor, direcao));
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
                    "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
                    "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )

    @Operation(summary = "Cadastrar nova Venda no sistema.", description = "Cadastrar nova Venda no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Consumidor ou produto nao encontrado"),
            @ApiResponse(responseCode = "422", description = "Campo obrigatorio ou invalido."),
            @ApiResponse(responseCode = "403", description = "Usuario nao possui permissao para esse recurso."),

    })

    @PostMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<VendaResponseDTO> criarVenda(
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId,
            @RequestBody @Valid CadastroVendaDTO dto
    ) {

        Venda venda = vendaService.criarVenda(dto, empresaId, estabelecimentoId);

        return ResponseEntity.ok(vendaMapper.toResponseDTO(venda));
    }
}

