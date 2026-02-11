package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnderDestDTO {

    private String xLgr;
    private String nro;
    private String xCpl;
    private String xBairro;

    private String cMun;
    private String xMun;
    private String UF;

    private String CEP;

    private String cPais; // "1058"
    private String xPais; // "Brasil"

    private String fone;
}
