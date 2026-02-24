package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icms;

// ributação pelo ICMS

import lombok.Data;

import java.math.BigDecimal;

@Data

public class ICMS00DTO {

    /*
    Origem da mercadoria (obrigatório)
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

    /* Tributção pelo ICMS (obrigatório)
    00 - Tributada integralmente*/
    private String cst;

    /* Modalidade de determinação da BC do ICMS (obrigatório):
    0 - Margem Valor Agregado (%%)
    1 - Pauta (valor)
    2 - Preço Tabelado Máximo (valor)
    3 - Valor da Operação*/
    private Integer modBC;

    /* Valor da base de cálculo do ICMS. */
    private BigDecimal vBC;

    /* Alíquota do ICMS. */
    private BigDecimal pICMS;

    /* Valor do ICMS */
    private BigDecimal vICMS;

    /* Percentual de ICMS relativo ao Fundo de Combate à Pobreza (FCP). */
    private BigDecimal pFCP;

    /* Valor do ICMS relativo ao Fundo de Combate à Pobreza (FCP). */
    private BigDecimal vFCP;


}
