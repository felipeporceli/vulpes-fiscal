package com.vulpesfiscal.demo.controllers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.NfceDTO;
import com.vulpesfiscal.demo.controllers.mappers.VendaMapper;
import com.vulpesfiscal.demo.entities.Venda;
import com.vulpesfiscal.demo.services.NfceService;
import com.vulpesfiscal.demo.services.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teste")
public class TesteNfceController {

    private final NfceService nfceService;
    private final VendaMapper vendaMapper;
    private final VendaService vendaService;


    public TesteNfceController(NfceService nfceService, VendaMapper vendaMapper, VendaService vendaService) {
        this.nfceService = nfceService;
        this.vendaMapper = vendaMapper;
        this.vendaService = vendaService;
    }

    @PostMapping("/empresa/{empresaId}/nfce/{estabelecimentoId}")
    public ResponseEntity<NfceDTO> testarNfce(@RequestBody CadastroVendaDTO vendaDTO,
                                              @PathVariable Integer estabelecimentoId,
                                              @PathVariable Integer empresaId) {

        Venda venda = vendaService.criarVenda(
                vendaDTO,
                empresaId,
                estabelecimentoId
        );
        NfceDTO nfce = nfceService.gerarNfce(venda, estabelecimentoId);

        return ResponseEntity.ok(nfce);
    }
}

