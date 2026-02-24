package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class COFINSAliqDTO {
    private String CST;
    private BigDecimal vBC;
    private BigDecimal pCOFINS;
    private BigDecimal vCOFINS;
}
