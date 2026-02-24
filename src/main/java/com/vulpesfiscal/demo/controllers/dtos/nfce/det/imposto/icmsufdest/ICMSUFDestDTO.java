package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icmsufdest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ICMSUFDestDTO {
    private BigDecimal vBCUFDest;
    private BigDecimal vBCFCPUFDest;
    private BigDecimal pFCPUFDest;
    private BigDecimal pICMSUFDest;
    private BigDecimal pICMSInter;
    private BigDecimal pICMSInterPart;
    private BigDecimal vFCPUFDest;
    private BigDecimal vICMSUFDest;
    private BigDecimal vICMSUFRemet;

}
