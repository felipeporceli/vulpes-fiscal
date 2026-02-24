package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.issqn;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ISSQNDTO {
    private BigDecimal vBC = null;
    private BigDecimal vAliq = null;
    private BigDecimal vISSQN = null;
    private String cMunFG = null;
    private String cListServ = null;
    private BigDecimal vDeducao = null;
    private BigDecimal vOutro = null;
    private BigDecimal vDescIncond = null;
    private BigDecimal vDescCond = null;
    private BigDecimal vISSRet = null;
    private Integer indISS = null;
    private String cServico = null;
    private String cMun = null;
    private String cPais = null;
    private String nProcesso = null;
    private Integer indIncentivo = null;
}
