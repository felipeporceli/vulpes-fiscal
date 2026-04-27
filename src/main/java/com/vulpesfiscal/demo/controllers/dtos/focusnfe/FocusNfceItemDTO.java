package com.vulpesfiscal.demo.controllers.dtos.focusnfe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FocusNfceItemDTO {

    private Integer numeroItem;
    private String codigoProduto;
    private String descricao;
    private String cfop;
    private String codigoNcm;

    private String unidadeComercial;
    private BigDecimal quantidadeComercial;
    private BigDecimal valorUnitarioComercial;
    private BigDecimal valorBruto;

    // Tributáveis (iguais ao comercial quando não diferenciados)
    private String unidadeTributavel;
    private BigDecimal quantidadeTributavel;
    private BigDecimal valorUnitarioTributavel;

    // Tributos
    private Integer icmsOrigem;
    private String icmsSituacaoTributaria;
    private String pisSituacaoTributaria;
    private String cofinsSituacaoTributaria;
}
