package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbsmono.gmonopadrao;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GMonoPadraoDTO {

    private BigDecimal qBCMono = null;
    private BigDecimal adRemIBS = null;
    private BigDecimal adRemCBS = null;
    private BigDecimal vIBSMono = null;
    private BigDecimal vCBSMono = null;

}
