package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.VendaResponseDTO;
import com.vulpesfiscal.demo.controllers.mappers.VendaMapper;
import com.vulpesfiscal.demo.entities.Venda;
import com.vulpesfiscal.demo.services.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;
    private final VendaMapper vendaMapper;

    @PostMapping("/empresa/{empresaId}/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<VendaResponseDTO> criarVenda(
            @PathVariable Integer empresaId,
            @PathVariable Integer estabelecimentoId,
            @RequestBody CadastroVendaDTO dto
    ) {

        Venda vendaCriada = vendaService.criarVenda(
                dto,
                empresaId,
                estabelecimentoId
        );

        return ResponseEntity.ok(
                vendaMapper.toResponseDTO(vendaCriada)
        );
    }
}

