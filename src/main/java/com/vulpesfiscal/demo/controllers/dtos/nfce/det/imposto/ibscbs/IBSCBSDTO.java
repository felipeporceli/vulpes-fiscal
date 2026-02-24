package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gcredpresoper.GCredPresOperDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbs.GIBSCBSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gibscbsmono.GIBSCBSMonoDTO;
import lombok.Data;

@Data
public class IBSCBSDTO {

    private String cst = null;
    private String cClassTrib = null;
    private Integer indDoacao = null;
    private GIBSCBSDTO gIBSCSB = null;
    private GIBSCBSMonoDTO gibscbsMono = null;
    private GTransfCredDTO gTransfCred = null;
    private GAjusteCompetDTO gAjusteCompet = null;
    private GEstornoCredDTO gEstornoCredDTO = null;
    private GCredPresOperDTO gCredPresOper = null;
    private GCredPresIBSZFMDTO gCredPresIBSZFM = null;

}
