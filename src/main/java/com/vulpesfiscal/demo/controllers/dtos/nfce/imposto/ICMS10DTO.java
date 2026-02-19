package com.vulpesfiscal.demo.controllers.dtos.nfce.imposto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ICMS10DTO {

    /*Origem da mercadoria (obrigatorio):
    0 - Nacional, exceto as indicadas nos códigos 3, 4, 5 e 8;
    1 - Estrangeira - Importação direta, exceto a indicada no código 6;
    2 - Estrangeira - Adquirida no mercado interno, exceto a indicada no código 7;
    3 - Nacional, mercadoria ou bem com Conteúdo de Importação superior a 40%% e inferior ou igual a 70%%;
    4 - Nacional, cuja produção tenha sido feita em conformidade com os processos produtivos básicos de que tratam as legislações citadas nos Ajustes;
    5 - Nacional, mercadoria ou bem com Conteúdo de Importação inferior ou igual a 40%%;
    6 - Estrangeira - Importação direta, sem similar nacional, constante em lista da CAMEX e gás natural;
    7 - Estrangeira - Adquirida no mercado interno, sem similar nacional, constante lista CAMEX e gás natural;
    8 - Nacional, mercadoria ou bem com Conteúdo de Importação superior a 70%%.*/
    private Integer orig;

    /* 10 - Tributada e com cobrança do ICMS por substituição tributária (obrigatorio)*/
    private String cst;

    /*Modalidade de determinação da BC do ICMS (obrigatorio):
    0 - Margem Valor Agregado (%%)
    1 - Pauta (valor)
    2 - Preço Tabelado Máximo (valor)
    3 - Valor da Operação*/
    private Integer modBC;

    /* Valor da base de cálculo do ICMS. (obrigatorio) */
    private BigDecimal vBC;

    /* Alíquota do ICMS. (obrigatorio) */
    private BigDecimal pICMS;

    /* Valor do ICMS (obrigatorio) */
    private BigDecimal vICMS;

    /* Valor da Base de cálculo do Fundo de Combate a Pobreza */
    private BigDecimal vBCFCP;

    /* Percentual de ICMS relativo ao Fundo de Combate à Pobreza (FCP). */
    private BigDecimal pFCP;

    /* Valor do ICMS relativo ao Fundo de Combate à Pobreza (FCP). */
    private BigDecimal vFCP;

    /* Modalidade de determinação da BC do ICMS ST (obrigatorio):
    0 - Preço tabelado ou máximo sugerido
    1 - Lista Negativa (valor)
    2 - Lista Positiva (valor)
    3 - Lista Neutra (valor)
    4 - Margem Valor Agregado (%%)
    5 - Pauta (valor)
    6 - Valor da Operação */
    private BigDecimal modBCST;


    /* Percentual da Margem de Valor Adicionado ICMS ST. */
    private BigDecimal pMVAST;

    /* Percentual de redução da BC ICMS ST. */
    private BigDecimal pRedBCST;

    /* Valor da BC do ICMS ST. (obrigatorio) */
    private BigDecimal vBCST;

    /* Alíquota do ICMS ST. (obrigatorio) */
    private BigDecimal pICMSST;

    /* Valor do ICMS ST. (obrigatorio) */
    private BigDecimal vICMSST;

    /* Valor da Base de cálculo do FCP retido por substituicao tributaria. */
    private BigDecimal vBCFCPST;

    /* Percentual de FCP retido por substituição tributária. */
    private BigDecimal pFCPST;

    /* Valor do FCP retido por substituição tributária. */
    private BigDecimal vFCPST;

    /* Valor do ICMS-ST desonerado. */
    private BigDecimal vICMSSTDeson;

    /*
    Motivo da desoneração do ICMS-ST: 3-Uso na agropecuária
    9 - Outros
    12 - Fomento agropecuário */
    private Integer motDesICMSST;


}
