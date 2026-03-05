package com.vulpesfiscal.demo.controllers.dtos.nfce.pagamento.card;

import lombok.Data;

@Data
public class CardDTO {

    private Integer tpIntegra;
    private String cnpj;
    private String tBand;
    private String cAut;
    private String cnpjReceb;
    private String idTermPag;

}
