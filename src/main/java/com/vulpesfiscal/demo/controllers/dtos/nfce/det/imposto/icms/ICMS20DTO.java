package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icms;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ICMS20DTO {
    private Integer orig = null;
    private String CST = null;
    private Integer modBC = null;
    private BigDecimal pRedBC = null;
    private BigDecimal vBC = null;
    private BigDecimal pICMS = null;
    private BigDecimal vICMS = null;
    private BigDecimal vBCFCP = null;
    private BigDecimal pFCP = null;
    private BigDecimal vFCP = null;
    private BigDecimal vICMSDeson = null;
    private Integer motDesICMS = null;
    private Integer indDeduzDeson = null;
}
