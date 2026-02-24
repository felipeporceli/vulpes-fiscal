package com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot;

import com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gcbs.GCBSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gestornocred.GEstornoCredDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gibs.GIBSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gibs.gibsmun.GIBSMunDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gibs.gibsuf.GIBSUfDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.ibscbstot.gmono.GMonoDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IBSCBSTotDTO {

    private BigDecimal vBCIBSCBS;
    private GIBSDTO gibs;
    private GCBSDTO gcbs;
    private GMonoDTO gMono;
    private GEstornoCredDTO gEstornoCred;

}
