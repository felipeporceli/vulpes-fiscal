package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gcbs.gred;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GRedDTO {

    private BigDecimal pRedAliq = null;
    private BigDecimal pAliqEfet = null;

}
