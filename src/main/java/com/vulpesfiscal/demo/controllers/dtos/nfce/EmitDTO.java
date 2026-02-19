package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmitDTO {

    private String CNPJ;   // Usar em 99% dos casos
    private String CPF;    // Só se for pessoa física (raríssimo em NFC-e)

    private String xNome;  // Razão social
    private String xFant;  // Nome fantasia

    private EnderEmitDTO enderEmit;

    private String IEST;   // IE do substituto tributário (quase nunca usado)
    private String IM;     // Inscrição Municipal
    private String CNAE;   // CNAE principal

    private Integer CRT;   // Regime tributário
}
