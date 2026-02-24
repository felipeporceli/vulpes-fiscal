package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gibsuf;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gibsuf.gdevtrib.GDevTribDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gibsuf.gdif.GDifDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.gibsuf.gred.GRedDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GIBSUfDTO {

    private BigDecimal pIBSUF;
    private GDifDTO gDif;
    private GDevTribDTO gDevTrib;
    private GRedDTO gRed;
    private BigDecimal vIBSUF;

}
