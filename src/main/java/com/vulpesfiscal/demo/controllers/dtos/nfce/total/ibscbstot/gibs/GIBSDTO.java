package com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gibs;

import com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gibs.gibsmun.GIBSMunDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gibs.gibsuf.GIBSUfDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GIBSDTO {

    private GIBSUfDTO gibsUf;
    private GIBSMunDTO gibsMun;
    private BigDecimal vIBS;
    private BigDecimal vCredPres;
    private BigDecimal vCredPresCondSus;

}
