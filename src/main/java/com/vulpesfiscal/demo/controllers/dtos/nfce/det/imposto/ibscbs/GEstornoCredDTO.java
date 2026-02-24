package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GEstornoCredDTO {

    private BigDecimal vIBSEstCred = null;
    private BigDecimal vCBSEstCred = null;

}
