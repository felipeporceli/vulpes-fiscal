package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gtribcompragov;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GTribCompraGovDTO {

    private BigDecimal pAliqIBSUF;
    private BigDecimal vTribIBSUF;
    private BigDecimal pAliqIBSMun;
    private BigDecimal vTribIBSMun;
    private BigDecimal pAliqCBS;
    private BigDecimal vTribCBS;

}
