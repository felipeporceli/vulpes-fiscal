package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class COFINSOutrDTO {
    private String CST;
    private BigDecimal vBC;
    private BigDecimal pCOFINS;
    private BigDecimal qBCProd;
    private BigDecimal vAliqProd;
    private BigDecimal vCOFINS;
}
