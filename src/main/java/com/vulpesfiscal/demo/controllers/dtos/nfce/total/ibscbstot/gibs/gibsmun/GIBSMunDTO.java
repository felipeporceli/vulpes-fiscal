package com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gibs.gibsmun;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GIBSMunDTO {

    private BigDecimal vDif = null;
    private BigDecimal vDevTrib = null;
    private BigDecimal vIBSMun = null;

}
