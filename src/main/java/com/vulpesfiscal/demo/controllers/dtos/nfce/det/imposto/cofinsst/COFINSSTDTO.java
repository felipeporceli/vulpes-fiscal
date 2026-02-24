package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofinsst;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class COFINSSTDTO {
    private BigDecimal vBC;
    private BigDecimal pCOFINS;
    private BigDecimal qBCProd;
    private BigDecimal vAliqProd;
    private BigDecimal vCOFINS;
    private Integer indSomaCOFINSST;
}
