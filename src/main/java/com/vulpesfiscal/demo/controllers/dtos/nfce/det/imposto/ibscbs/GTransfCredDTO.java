package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GTransfCredDTO {

    private BigDecimal vIBS = null;
    private BigDecimal vCBS = null;

}
