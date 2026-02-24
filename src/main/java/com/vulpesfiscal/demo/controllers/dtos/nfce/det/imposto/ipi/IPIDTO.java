package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ipi;

import lombok.Data;

@Data
public class IPIDTO {
    private String cnpjProd;
    private String cSelo;
    private String qSelo;
    private String cEnq;
    private IPIntDTO ipIntDTO;
    private IPITribDTO ipiTribDTO;
}
