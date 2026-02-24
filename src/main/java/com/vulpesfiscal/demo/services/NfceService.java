package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.nfce.*;
import com.vulpesfiscal.demo.controllers.mappers.NfceMapper;
import com.vulpesfiscal.demo.entities.*;
import com.vulpesfiscal.demo.repositories.NfceRepository;
import com.vulpesfiscal.demo.repositories.UsuarioRepository;
import com.vulpesfiscal.demo.services.nfce.*;
import com.vulpesfiscal.demo.services.nfce.det.ImpostoService;
import com.vulpesfiscal.demo.services.nfce.total.TotalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NfceService {

    private final NfceRepository nfceRepository;
    private final EmpresaService empresaService;
    private final DestinatarioService destinatarioService;
    private final ProdService prodService;
    private final EstabelecimentoService estabelecimentoService;
    private final EmitenteService emitenteService;
    private final IdeService ideService;
    private final NfceMapper nfceMapper;
    private final UsuarioRepository usuarioRepository;
    private final ImpostoService impostoService;
    private final TotalService totalService;

    /**
     * Emite NFC-e a partir de uma Venda
     * Só cria NFC-e se venda.emitirNfce == true
     */



    // ======================
    // MÉTODOS AUXILIARES
    // ======================
    private void validarVenda(Venda venda) {

        if (venda.getEmpresa() == null) {
            throw new IllegalArgumentException("Venda sem empresa vinculada");
        }

        if (venda.getEstabelecimento() == null) {
            throw new IllegalArgumentException("Venda sem estabelecimento vinculado");
        }

        if (!venda.getEstabelecimento().getEmpresa().getId()
                .equals(venda.getEmpresa().getId())) {
            throw new IllegalArgumentException(
                    "Estabelecimento não pertence à empresa informada"
            );
        }

        if (venda.getValorTotal() == null || venda.getValorTotal().signum() <= 0) {
            throw new IllegalArgumentException("Valor total da venda inválido");
        }
    }

    public NfceDTO gerarNfce(Venda venda, Integer estabelecimentoId) {

        NfceDTO nfce = new NfceDTO();

        nfce.setVersao("v4.00");
        nfce.setId(null);
        nfce.setDest(destinatarioService.gerarDestinatario(venda));
        nfce.setEmit(emitenteService.gerarEmitente(venda, estabelecimentoId));
        nfce.setIde(ideService.gerarIdentificacaoNfce(venda));
        nfce.setTotal(totalService.gerarTotal());

        // retirada, entrega, avulsa = null
        nfce.setDet(prodService.montarItens(venda));;



        return nfce;
    }





}

