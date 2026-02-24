package com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gibs.gibsuf;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GIBSUfDTO {

    private BigDecimal vDif = null;
    private BigDecimal vDevTrib = null;
    private BigDecimal vIBSUF = null;

}
