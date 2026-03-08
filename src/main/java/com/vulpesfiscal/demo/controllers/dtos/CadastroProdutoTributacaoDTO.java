package com.vulpesfiscal.demo.controllers.dtos;

import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CadastroProdutoTributacaoDTO {
    public Integer idProduto;
    public String uf;
    public String cfop;
    public String cstIcms;
    public String csosnIcms;
    public BigDecimal aliquotaIcms;
    public BigDecimal pFcp;
    public BigDecimal pRedBc;
    public Boolean temStAnterior;
    public String cstPis;
    public BigDecimal aliquotaPis;
    public String cstCofins;
    public BigDecimal aliquotaCofins;
    public RegimeTributarioEmpresa regimeTributarioEmpresa;
}