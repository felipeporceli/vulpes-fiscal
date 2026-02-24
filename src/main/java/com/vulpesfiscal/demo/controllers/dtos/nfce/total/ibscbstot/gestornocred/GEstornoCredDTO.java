package com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gestornocred;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GEstornoCredDTO {

    private BigDecimal vIBSEstCred = null;
    private BigDecimal vCBSEstCred = null;

}
