package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProdDTO {

    // Identificação

    // Código interno
    private String cProd;

    // Código de barras
    private String cEAN;

    // Descrição
    private String xProd;

    // NCM do produto
    private String NCM;

    // CFOP do produto (natureza da operacao)
    private String CFOP;

    // Comercial

    // Unidade comercial
    private String uCom;

    // Quantidade comercial
    private BigDecimal qCom;

    // Valor unituário
    private BigDecimal vUnCom;

    // Valor total
    private BigDecimal vProd;

    // Tributável
    private String cEANTrib;
    private String uTrib;
    private BigDecimal qTrib;
    private BigDecimal vUnTrib;

    // Totalização
    private Integer indTot;
}
