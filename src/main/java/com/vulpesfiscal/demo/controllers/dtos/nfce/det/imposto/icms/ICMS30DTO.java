package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icms;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ICMS30DTO {
    private Integer orig = null;
    private String CST = null;
    private Integer modBCST = null;
    private BigDecimal pMVAST = null;
    private BigDecimal pRedBCST = null;
    private BigDecimal vBCST = null;
    private BigDecimal pICMSST = null;
    private BigDecimal vICMSST = null;
    private BigDecimal vBCFCPST = null;
    private BigDecimal pFCPST = null;
    private BigDecimal vFCPST = null;
    private BigDecimal vICMSDeson = null;
    private Integer motDesICMS = null;
    private Integer indDeduzDeson = null;
}
