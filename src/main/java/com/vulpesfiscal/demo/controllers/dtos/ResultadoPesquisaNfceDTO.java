package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.Nfce;
import com.vulpesfiscal.demo.entities.enums.StatusNfce;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record ResultadoPesquisaNfceDTO(
        Integer id,
        Integer empresaId,
        Integer estabelecimentoId,
        String numero,
        Integer serie,
        String chaveAcesso,
        BigDecimal valorTotal,
        StatusNfce statusNfce,
        String protocoloAutorizacao,
        OffsetDateTime dataEmissao,
        LocalDateTime dataCriacao
) {
    public static ResultadoPesquisaNfceDTO fromEntity(Nfce n) {
        return new ResultadoPesquisaNfceDTO(
                n.getId(),
                n.getEmpresa().getId(),
                n.getEstabelecimento().getId(),
                n.getNumero(),
                n.getSerie(),
                n.getChaveAcesso(),
                n.getValorTotal(),
                n.getStatusNfce(),
                n.getProtocoloAutorizacao(),
                n.getDataEmissao(),
                n.getDataCriacao()
        );
    }
}
