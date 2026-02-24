package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gtribregular;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GTribRegularDTO {

    private String CSTReg;
    private String cClassTribReg;
    private BigDecimal pAliqEfetRegIBSUF;
    private BigDecimal vTribRegIBSUF;
    private BigDecimal pAliqEfetRegIBSMun;
    private BigDecimal vTribRegIBSMun;
    private BigDecimal pAliqEfetRegCBS;
    private BigDecimal vTribRegCBS;

}
