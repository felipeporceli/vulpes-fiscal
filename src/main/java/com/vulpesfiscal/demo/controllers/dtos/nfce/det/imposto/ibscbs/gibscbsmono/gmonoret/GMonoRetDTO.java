package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbsmono.gmonoret;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GMonoRetDTO {

    private BigDecimal qBCMonoRet = null;
    private BigDecimal adRemIBSRet = null;
    private BigDecimal vIBSMonoRet = null;
    private BigDecimal adRemCBSRet = null;
    private BigDecimal vCBSMonoRet = null;

}
