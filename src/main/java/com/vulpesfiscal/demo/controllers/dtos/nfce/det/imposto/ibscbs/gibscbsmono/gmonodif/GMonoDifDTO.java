package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbsmono.gmonodif;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GMonoDifDTO {

    private BigDecimal pDifIBS = null;
    private BigDecimal vIBSMonoDif = null;
    private BigDecimal pDifCBS = null;
    private BigDecimal vCBSMonoDif = null;

}
