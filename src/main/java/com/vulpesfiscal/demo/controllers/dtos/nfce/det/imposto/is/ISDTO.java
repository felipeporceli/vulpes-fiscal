package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.is;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ISDTO {

    private String CSTIS;
    private String cClassTribIS;
    private BigDecimal vBCIS;
    private BigDecimal pIS;
    private BigDecimal pISEspec;
    private String uTrib;
    private BigDecimal qTrib;
    private BigDecimal vIS;

}
