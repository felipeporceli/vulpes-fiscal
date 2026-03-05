package com.vulpesfiscal.demo.controllers.dtos.nfce;

public class NfceResponseDTO {

    private String ambiente;
    private InfNFe infNFe;

    public NfceResponseDTO(String ambiente, InfNFe infNFe) {
        this.ambiente = ambiente;
        this.infNFe = infNFe;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public InfNFe getInfNFe() {
        return infNFe;
    }
}
