package com.vulpesfiscal.demo.controllers.dtos.nfce.total.issqntot;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ISSQNtotDTO {

    private BigDecimal vServ = null;
    private BigDecimal vBC = null;
    private BigDecimal vISS = null;
    private BigDecimal vPIS = null;
    private BigDecimal vCOFINS = null;
    private LocalDate dCompet = null;
    private BigDecimal vDeducao = null;
    private BigDecimal vOutro = null;
    private BigDecimal vDescIncond = null;
    private BigDecimal vDescCond = null;
    private BigDecimal vISSRet = null;
    private Integer cRegTrib = null;

}
