package com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gmono;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GMonoDTO {

    private BigDecimal vIBSMono = null;
    private BigDecimal vCBSMono = null;
    private BigDecimal vIBSMonoReten = null;
    private BigDecimal vCBSMonoReten = null;
    private BigDecimal vIBSMonoRet = null;
    private BigDecimal vCBSMonoRet = null;

}
