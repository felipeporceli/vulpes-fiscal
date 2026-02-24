package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gcbs;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gcbs.gdevtrib.GDevTribDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gcbs.gdif.GDifDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gcbs.gred.GRedDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GCBSDTO {

    private BigDecimal pCBS = null;
    private GDifDTO gDif = null;
    private GRedDTO gRed = null;
    private GDevTribDTO gDevTrib = null;

}
