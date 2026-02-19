package com.vulpesfiscal.demo.controllers.dtos.nfce.imposto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ICMS61DTO {
    private Integer orig = null;
    private String CST = null;
    private BigDecimal qBCMonoRet = null;
    private BigDecimal adRemICMSRet = null;
    private BigDecimal vICMSMonoRet = null;
}
