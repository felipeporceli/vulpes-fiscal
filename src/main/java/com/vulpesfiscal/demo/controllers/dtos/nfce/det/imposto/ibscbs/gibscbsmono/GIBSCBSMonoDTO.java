package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbsmono;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbsmono.gmonopadrao.GMonoPadraoDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbsmono.gmonodif.GMonoDifDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbsmono.gmonoret.GMonoRetDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbsmono.gmonoreten.GMonoRetenDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GIBSCBSMonoDTO {

    private GMonoPadraoDTO gMonoPadrao;
    private GMonoRetenDTO gMonoRetenDTO;
    private GMonoRetDTO gMonoRet;
    private GMonoDifDTO gMonoDif;
    private BigDecimal vTotIBSMonoItem = null;
    private BigDecimal vTotCBSMonoItem = null;

}
