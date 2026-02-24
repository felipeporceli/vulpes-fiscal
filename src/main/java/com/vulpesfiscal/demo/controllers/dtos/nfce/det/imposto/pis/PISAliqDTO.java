package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pis;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PISAliqDTO {
    private String CST = null;
    private BigDecimal vBC = null;
    private BigDecimal pPIS = null;
    private BigDecimal vPIS = null;
}
