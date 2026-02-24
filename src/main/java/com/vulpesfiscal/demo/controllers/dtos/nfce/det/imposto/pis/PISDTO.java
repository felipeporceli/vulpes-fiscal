package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pis;

import lombok.Data;

@Data
public class PISDTO {
    private PISAliqDTO pisAliq = null;
    private PISNTDTO pisnt = null;
    private PISOutrDTO pisOutr = null;
    private PISQtdeDTO pisQtde = null;
}
