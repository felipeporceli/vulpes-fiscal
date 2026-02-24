package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.II.IIDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins.COFINSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofinsst.COFINSSTDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.IBSCBSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icms.ICMSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icmsufdest.ICMSUFDestDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ipi.IPIDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.is.ISDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.issqn.ISSQNDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pis.PISDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pisst.PISSTDTO;
import lombok.Data;

@Data
public class ImpostoDTO {
    private ICMSDTO icms;
    private IPIDTO ipi;
    private IIDTO ii;
    private ISSQNDTO issqn;
    private PISDTO pis;
    private PISSTDTO pisst;
    private COFINSDTO cofins;
    private COFINSSTDTO cofinsst;
    private ICMSUFDestDTO icmsufDest;
    private ISDTO is;
    private IBSCBSDTO ibscbs;
}
