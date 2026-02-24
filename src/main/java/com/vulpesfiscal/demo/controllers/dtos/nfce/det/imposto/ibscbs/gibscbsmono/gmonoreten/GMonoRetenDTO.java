package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbsmono.gmonoreten;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GMonoRetenDTO {

    private BigDecimal qBCMonoReten = null;
    private BigDecimal adRemIBSReten = null;
    private BigDecimal vIBSMonoReten = null;
    private BigDecimal adRemCBSReten = null;
    private BigDecimal vCBSMonoReten = null;

}
