package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnderEmitDTO {

    private String xLgr;     // Logradouro
    private String nro;      // Número
    private String xCpl;     // Complemento
    private String xBairro;  // Bairro

    private String cMun;     // Código IBGE do município
    private String xMun;     // Nome do município
    private String UF;       // Sigla do estado

    private String CEP;

    private String cPais;    // Normalmente "1058"
    private String xPais;    // Normalmente "Brasil"

    private String fone;
}
