package com.vulpesfiscal.demo.controllers.dtos.nfce.total.rettrib;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RetTribDTO {

    private BigDecimal vRetPIS = null;
    private BigDecimal vRetCOFINS = null;
    private BigDecimal vRetCSLL = null;
    private BigDecimal vBCIRRF = null;
    private BigDecimal vIRRF = null;
    private BigDecimal vBCRetPrev = null;
    private BigDecimal vRetPrev = null;

}
