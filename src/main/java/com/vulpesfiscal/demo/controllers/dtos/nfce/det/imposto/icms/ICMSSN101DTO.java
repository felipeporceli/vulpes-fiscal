package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icms;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ICMSSN101DTO {
    private Integer orig = null;
    private String CSOSN = null;
    private BigDecimal pCredSN = null;
    private BigDecimal vCredICMSSN = null;
}
