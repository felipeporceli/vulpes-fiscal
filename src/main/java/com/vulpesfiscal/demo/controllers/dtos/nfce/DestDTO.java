package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DestDTO {

    private String CNPJ;          // Pessoa jurídica
    private String CPF;           // Pessoa física
    private String idEstrangeiro; // Estrangeiro (raríssimo em NFC-e)

    private String xNome;         // Nome do destinatário (opcional em NFC-e)

    private EnderDestDTO enderDest;

    private Integer indIEDest;    // Indicador da IE
    private String IE;            // Inscrição Estadual
    private String ISUF;          // SUFRAMA
    private String IM;            // Inscrição Municipal

    private String email;
}

