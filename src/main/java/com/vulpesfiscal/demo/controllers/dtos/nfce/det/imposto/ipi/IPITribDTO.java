package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ipi;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IPITribDTO {
    private String CST = null;
    private BigDecimal vBC = null;
    private BigDecimal pIPI = null;
    private BigDecimal qUnid = null;
    private BigDecimal vUnid = null;
    private BigDecimal vIPI = null;
}
