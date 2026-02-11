package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutXMLDTO {

    private String CNPJ;
    private String CPF;
}
