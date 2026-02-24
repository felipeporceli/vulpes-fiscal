package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class COFINSQtdeDTO {
    private String CST;
    private BigDecimal qBCProd;
    private BigDecimal vAliqProd;
    private BigDecimal vCOFINS;
}
