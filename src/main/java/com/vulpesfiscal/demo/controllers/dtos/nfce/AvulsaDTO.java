package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvulsaDTO {

    private String CNPJ;     // CNPJ do órgão emissor
    private String xOrgao;   // Nome do órgão
    private String matr;     // Matrícula do agente
    private String xAgente;  // Nome do agente emissor
    private String fone;     // Telefone
    private String UF;       // UF do órgão

    private String nDAR;     // Número do DAR (Documento de Arrecadação)

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dEmi;  // Data de emissão do DAR

    private BigDecimal vDAR; // Valor do DAR

    private String repEmi;   // Repartição emissora

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dPag;  // Data de pagamento
}

