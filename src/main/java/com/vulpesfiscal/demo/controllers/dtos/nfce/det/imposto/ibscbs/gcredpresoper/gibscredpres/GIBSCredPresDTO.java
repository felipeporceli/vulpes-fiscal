package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gcredpresoper.gibscredpres;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GIBSCredPresDTO {

    private BigDecimal pCredPres;
    private BigDecimal vCredPres;
    private BigDecimal vCredPresCondSus;

}
