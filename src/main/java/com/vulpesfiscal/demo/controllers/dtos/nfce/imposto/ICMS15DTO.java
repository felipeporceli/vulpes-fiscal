package com.vulpesfiscal.demo.controllers.dtos.nfce.imposto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ICMS15DTO {

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

    /* Tributção pelo ICMS (obrigatorio)
    15 - Tributação monofásica própria e com responsabilidade pela retenção sobre combustíveis */
    private String cst;

    /* Quantidade tributada. */
    private BigDecimal qBCMono = null;

    /* Alíquota ad rem do imposto. (obrigatorio)*/
    private BigDecimal adRemICMS = null;

    /* Valor do ICMS próprio. (obrigatorio)*/
    private BigDecimal vICMSMono = null;

    /* Quantidade tributada sujeita a retenção. */
    private BigDecimal qBCMonoReten = null;

    /* Alíquota ad rem do imposto com retenção. (obrigatorio) */
    private BigDecimal adRemICMSReten = null;

    /* Valor do ICMS com retenção. (obrigatorio) */
    private BigDecimal vICMSMonoReten = null;

    /* Percentual de redução do valor da alíquota ad rem do ICMS. */
    private BigDecimal pRedAdRem = null;

    /* Motivo da redução do adrem
    1 - Transporte coletivo de passageiros
    9 - Outros */
    private Integer motRedAdRem = null;

}
