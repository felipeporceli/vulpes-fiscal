package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.VendaResponseDTO;
import com.vulpesfiscal.demo.controllers.mappers.VendaMapper;
import com.vulpesfiscal.demo.entities.Venda;
import com.vulpesfiscal.demo.services.NfceService;
import com.vulpesfiscal.demo.services.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
@Tag(name = "Vendas")
public class VendaController {

    private final VendaService vendaService;
    private final VendaMapper vendaMapper;
    private final NfceService nfceService;

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

        Venda venda = vendaService.criarVenda(
                dto,
                empresaId,
                estabelecimentoId
        );

        if (dto.emitirNfce() == true) {
            nfceService.gerarNfce(venda, estabelecimentoId);
        }

        return ResponseEntity.ok(
                vendaMapper.toResponseDTO(venda)
        );
    }
}

