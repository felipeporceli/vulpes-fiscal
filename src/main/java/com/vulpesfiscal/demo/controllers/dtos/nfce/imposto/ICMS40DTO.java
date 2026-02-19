package com.vulpesfiscal.demo.controllers.dtos.nfce.imposto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ICMS40DTO {
    private Integer orig = null;
    private String CST = null;
    private BigDecimal vICMSDeson = null;
    private Integer motDesICMS = null;
    private Integer indDeduzDeson = null;
}
