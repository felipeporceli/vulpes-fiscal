package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaNfceDTO;
import com.vulpesfiscal.demo.entities.enums.StatusNfce;
import com.vulpesfiscal.demo.services.NfceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/nfce")
@RequiredArgsConstructor
@Tag(name = "NFC-e")
public class NfceController {

    private final NfceService nfceService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPORTE')")
    @Operation(summary = "Pesquisa global de NFC-e.", description = "Pesquisa NFC-e sem restrição de empresa (ADMINISTRADOR/SUPORTE).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para esse recurso."),
    })
    @GetMapping
    public ResponseEntity<Page<ResultadoPesquisaNfceDTO>> pesquisaGlobal(
            @RequestParam(value = "empresa-id",          required = false) Integer empresaId,
            @RequestParam(value = "estabelecimento-id",  required = false) Integer estabelecimentoId,
            @RequestParam(value = "status",              required = false) StatusNfce statusNfce,
            @RequestParam(value = "chave-acesso",        required = false) String chaveAcesso,
            @RequestParam(value = "numero",              required = false) String numero,
            @RequestParam(value = "data-inicio",         required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(value = "data-fim",            required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(value = "pagina",              defaultValue = "0")  Integer pagina,
            @RequestParam(value = "tamanho-pagina",      defaultValue = "10") Integer tamanhoPagina
    ) {
        return ResponseEntity.ok(nfceService.pesquisar(
                empresaId, estabelecimentoId, statusNfce,
                chaveAcesso, numero, dataInicio, dataFim,
                pagina, tamanhoPagina));
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
            "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
            "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Pesquisa NFC-e por empresa.", description = "Pesquisa NFC-e filtradas pela empresa do usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para esse recurso."),
    })
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<ResultadoPesquisaNfceDTO>> pesquisa(
            @PathVariable Integer empresaId,
            @RequestParam(value = "estabelecimento-id",  required = false) Integer estabelecimentoId,
            @RequestParam(value = "status",              required = false) StatusNfce statusNfce,
            @RequestParam(value = "chave-acesso",        required = false) String chaveAcesso,
            @RequestParam(value = "numero",              required = false) String numero,
            @RequestParam(value = "data-inicio",         required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(value = "data-fim",            required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(value = "pagina",              defaultValue = "0")  Integer pagina,
            @RequestParam(value = "tamanho-pagina",      defaultValue = "10") Integer tamanhoPagina
    ) {
        return ResponseEntity.ok(nfceService.pesquisar(
                empresaId, estabelecimentoId, statusNfce,
                chaveAcesso, numero, dataInicio, dataFim,
                pagina, tamanhoPagina));
    }

    @PreAuthorize(
            "hasAnyRole('ADMINISTRADOR','SUPORTE') or " +
            "(hasAnyRole('EMPRESARIO','GERENTE','CAIXA','VENDEDOR') and " +
            "#empresaId.toString() == principal.claims['empresaId'].toString())"
    )
    @Operation(summary = "Buscar NFC-e por ID.", description = "Retorna os detalhes de uma NFC-e específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "NFC-e encontrada."),
            @ApiResponse(responseCode = "404", description = "NFC-e não encontrada."),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para esse recurso."),
    })
    @GetMapping("/empresa/{empresaId}/{id}")
    public ResponseEntity<ResultadoPesquisaNfceDTO> buscarPorId(
            @PathVariable Integer empresaId,
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(nfceService.buscarPorId(id, empresaId));
    }
}
