package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gcredpresoper.gcbscredpres;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GCBSCredPresDTO {

    private BigDecimal pCredPres;
    private BigDecimal vCredPres;
    private BigDecimal vCredPresCondSus;

}
