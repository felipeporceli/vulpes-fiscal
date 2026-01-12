package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroNfceDTO;
import com.vulpesfiscal.demo.controllers.dtos.NfceResponseDTO;
import com.vulpesfiscal.demo.controllers.mappers.NfceMapper;
import com.vulpesfiscal.demo.controllers.mappers.NfceResponseMapper;
import com.vulpesfiscal.demo.entities.Nfce;
import com.vulpesfiscal.demo.services.NfceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nfce")
@RequiredArgsConstructor
public class NfceController {

    private final NfceService service;
    private final NfceResponseMapper nfceResponseMapper;

    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<NfceResponseDTO> criarNfce(
            @PathVariable Integer empresaId,
            @RequestParam Integer estabelecimentoId,
            @RequestParam Integer usuarioId,
            @RequestBody CadastroNfceDTO dto

    ) {
        Nfce nfce = service.gerarNfce(
                empresaId,
                estabelecimentoId,
                usuarioId,
                dto
        );

        NfceResponseDTO response = nfceResponseMapper.toDto(nfce);

        return ResponseEntity.ok(response);
    }
}


