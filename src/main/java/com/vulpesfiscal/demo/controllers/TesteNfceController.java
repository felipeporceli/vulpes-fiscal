package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.nfce.NfceDTO;
import com.vulpesfiscal.demo.entities.Venda;
import com.vulpesfiscal.demo.services.NfceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teste")
public class TesteNfceController {

    private final NfceService nfceService;

    public TesteNfceController(NfceService nfceService) {
        this.nfceService = nfceService;
    }

    @PostMapping("/nfce/{estabelecimentoId}")
    public ResponseEntity<NfceDTO> testarNfce(@RequestBody Venda venda,
                                              @PathVariable Integer estabelecimentoId) {

        NfceDTO nfce = nfceService.gerarNfce(venda, estabelecimentoId);

        return ResponseEntity.ok(nfce);
    }
}

