package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gcredpresoper;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gcredpresoper.gcbscredpres.GCBSCredPresDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs.gcredpresoper.gibscredpres.GIBSCredPresDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GCredPresOperDTO {

    private BigDecimal vBCCredPres = null;
    private String cCredPres = null;
    private GIBSCredPresDTO gibsCredPres = null;
    private GCBSCredPresDTO gcbsCredPres = null;

}
