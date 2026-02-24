package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GAjusteCompetDTO {

    private String competApur = null;
    private BigDecimal vIBS = null;
    private BigDecimal vCBS = null;

}
