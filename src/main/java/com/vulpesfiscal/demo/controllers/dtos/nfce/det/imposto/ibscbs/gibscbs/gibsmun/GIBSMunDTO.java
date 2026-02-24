package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gibsmun;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gibsmun.gdevtrib.GDevTribDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gibsmun.gdif.GDifDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gibsmun.gred.GRedDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GIBSMunDTO {

    private BigDecimal pIBSMun = null;
    private GDifDTO gDifDTO = null;
    private GDevTribDTO gDevTrib = null;
    private GRedDTO gRed = null;
}
