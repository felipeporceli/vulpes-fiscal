package com.vulpesfiscal.demo.controllers.dtos.nfce.imposto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ICMSSN500DTO {
    private Integer orig = null;
    private String CSOSN = null;
    private BigDecimal vBCSTRet = null;
    private BigDecimal pST = null;
    private BigDecimal vICMSSubstituto = null;
    private BigDecimal vICMSSTRet = null;
    private BigDecimal vBCFCPSTRet = null;
    private BigDecimal pFCPSTRet = null;
    private BigDecimal vFCPSTRet = null;
    private BigDecimal pRedBCEfet = null;
    private BigDecimal vBCEfet = null;
    private BigDecimal pICMSEfet = null;
    private BigDecimal vICMSEfet = null;

}
