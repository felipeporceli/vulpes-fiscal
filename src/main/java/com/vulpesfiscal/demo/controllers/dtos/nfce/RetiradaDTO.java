package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RetiradaDTO {

    private String CNPJ;
    private String CPF;
    private String xNome;

    private String xLgr;
    private String nro;
    private String xCpl;
    private String xBairro;

    private String cMun;
    private String xMun;
    private String UF;
    private String CEP;

    private String cPais;
    private String xPais;

    private String fone;
    private String email;
    private String IE;
}
