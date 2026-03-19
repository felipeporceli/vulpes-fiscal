package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.ProdutoTributacao;

import java.math.BigDecimal;

public record ResultadoPesquisaProdutoTributacaoDTO(

        Long id,
        Integer empresaId,
        Integer idProduto,
        String descricaoProduto,
        String uf,
        String cfop,
        String cstIcms,
        String csosnIcms,
        BigDecimal aliquotaIcms,
        BigDecimal pFcp,
        BigDecimal pRedBc,
        Boolean temStAnterior,
        String cstPis,
        BigDecimal aliquotaPis,
        String cstCofins,
        BigDecimal aliquotaCofins

) {

    public static ResultadoPesquisaProdutoTributacaoDTO fromEntity(ProdutoTributacao entity) {
        return new ResultadoPesquisaProdutoTributacaoDTO(
                entity.getId(),
                entity.getEmpresa().getId(),
                entity.getProduto().getIdProduto(),
                entity.getProduto().getDescricao(),
                entity.getUf(),
                entity.getCfop(),
                entity.getCstIcms(),
                entity.getCsosnIcms(),
                entity.getAliquotaIcms(),
                entity.getPFcp(),
                entity.getPRedBc(),
                entity.getTemStAnterior(),
                entity.getCstPis(),
                entity.getAliquotaPis(),
                entity.getCstCofins(),
                entity.getAliquotaCofins()
        );
    }
}
