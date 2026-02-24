package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pisst;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PISSTDTO {
    private BigDecimal vBC = null;
    private BigDecimal pPIS = null;
    private BigDecimal qBCProd = null;
    private BigDecimal vAliqProd = null;
    private BigDecimal vPIS = null;
    private Integer indSomaPISST = null;
}
