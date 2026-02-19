package com.vulpesfiscal.demo.services.nfce;

import com.vulpesfiscal.demo.controllers.dtos.nfce.IdeDTO;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Venda;
import com.vulpesfiscal.demo.repositories.NfceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;


@RequiredArgsConstructor
@Service
public class IdeService {

    private final NfceRepository nfceRepository;

    public IdeDTO gerarIdentificacaoNfce (Venda venda) {
        Estabelecimento estabelecimento = venda.getEstabelecimento();


        IdeDTO dto = IdeDTO.builder()
                .cUF(estabelecimento.getCodUf())
                .natOp("Venda de Mercadoria")
                .mod(65)
                .serie(1)
                .nNF(gerarNumero(venda.getEstabelecimento(), 1))
                .dhEmi(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")).withOffsetSameInstant(ZoneOffset.UTC))
                .dhSaiEnt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")).withOffsetSameInstant(ZoneOffset.UTC))
                .tpNF(1)
                .idDest(1)
                .cMunFG(estabelecimento.getMunicipioId())
                .tpImp(4)
                .tpEmis(1)
                .tpAmb(2)
                .finNFe(1)
                .indFinal(1)
                .indPres(1)
                .procEmi(0)
                .verProc("Vulpes Fiscal 0.0.1 beta")
                .build();

        return dto;
    }

    public Integer gerarNumero(Estabelecimento estabelecimento, Integer serie) {

        Integer ultimoNumero = nfceRepository.buscarUltimoNumero(
                estabelecimento.getId(),
                serie
        );

        Integer proximoNumero = (ultimoNumero == null ? 1 : ultimoNumero + 1);

        return proximoNumero;
    }



}
