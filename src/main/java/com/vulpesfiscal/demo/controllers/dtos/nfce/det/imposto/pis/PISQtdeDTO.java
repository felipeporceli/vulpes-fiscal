package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pis;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PISQtdeDTO {
    private String CST = null;
    private BigDecimal qBCProd = null;
    private BigDecimal vAliqProd = null;
    private BigDecimal vPIS = null;
}
