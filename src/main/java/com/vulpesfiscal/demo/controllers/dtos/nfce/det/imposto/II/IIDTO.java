package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.II;

import lombok.Data;

import java.math.BigDecimal;

// Imposto sobre importação
@Data
public class IIDTO {
    private BigDecimal vBC;
    private BigDecimal vDespAdu;
    private BigDecimal vII;
    private BigDecimal vIOF;
}
