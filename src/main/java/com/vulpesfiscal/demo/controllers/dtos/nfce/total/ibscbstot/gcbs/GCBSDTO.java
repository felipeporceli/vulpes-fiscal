package com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gcbs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GCBSDTO {

    private BigDecimal vDif = null;
    private BigDecimal vDevTrib = null;
    private BigDecimal vCBS = null;
    private BigDecimal vCredPres = null;
    private BigDecimal vCredPresCondSus = null;

}
