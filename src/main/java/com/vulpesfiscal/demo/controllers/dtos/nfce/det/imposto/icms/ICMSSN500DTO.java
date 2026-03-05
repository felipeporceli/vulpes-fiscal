package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icms;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ICMSSN500DTO {

    /*
    0   Nacional
    1	Estrangeira – importação direta
    2	Estrangeira – adquirida no mercado interno
    3	Nacional com conteúdo importação > 40%
    4	Nacional produção conforme PPB
    5	Nacional com conteúdo ≤ 40%
    6	Estrangeira importação direta sem similar
    7	Estrangeira mercado interno sem similar
    8	Nacional com conteúdo > 70% */

    private Integer orig = null;
    private String CSOSN = null;
    private BigDecimal vBCSTRet = null;
    private BigDecimal pST = null;
    private BigDecimal vICMSSubstituto;
    private BigDecimal vICMSSTRet = null;
    private BigDecimal vBCFCPSTRet = null;
    private BigDecimal pFCPSTRet = null;
    private BigDecimal vFCPSTRet = null;
    private BigDecimal pRedBCEfet = null;
    private BigDecimal vBCEfet = null;
    private BigDecimal pICMSEfet = null;
    private BigDecimal vICMSEfet = null;

}
