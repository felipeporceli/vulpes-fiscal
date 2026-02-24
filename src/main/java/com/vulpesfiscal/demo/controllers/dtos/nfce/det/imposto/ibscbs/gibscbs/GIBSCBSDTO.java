package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gcbs.GCBSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gibsmun.GIBSMunDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gibsuf.GIBSUfDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gtribcompragov.GTribCompraGovDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gtribregular.GTribRegularDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GIBSCBSDTO {
    private BigDecimal vBC;
    private GIBSUfDTO gibsUf;
    private GIBSMunDTO gibsMun;
    private BigDecimal vIBS;
    private GCBSDTO gCBS;
    private GTribRegularDTO gTribRegular;
    private GTribCompraGovDTO gTribCompraGov;

}
