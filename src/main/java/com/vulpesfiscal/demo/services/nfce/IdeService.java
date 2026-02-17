package com.vulpesfiscal.demo.services.nfce;

import com.vulpesfiscal.demo.controllers.dtos.nfce.IdeDTO;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Venda;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;


@Service
public class IdeService {

    public IdeDTO gerarIdentificacaoNfce (Venda venda) {
        Estabelecimento estabelecimento = venda.getEstabelecimento();

        IdeDTO dto = IdeDTO.builder()
                .cUF(estabelecimento.getCodUf())
                .natOp("Venda de Mercadoria")
                .mod("65")
                .serie("1")
                .nNF(String.valueOf(venda.getNfce().getNumero()))
                .dhEmi(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .dhSaiEnt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .tpNF("1")
                .idDest("1")
                .cMunFG(estabelecimento.getMunicipioId())
                .tpImp("4")
                .tpEmis("1")
                .tpAmb("2")
                .finNFe("1")
                .indFinal("1")
                .indPres("1")
                .procEmi("0")
                .verProc("Vulpes Fiscal 0.0.1 beta")
                .build();

        return dto;
    }


}
