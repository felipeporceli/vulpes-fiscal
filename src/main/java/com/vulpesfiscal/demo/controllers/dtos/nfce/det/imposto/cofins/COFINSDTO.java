package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins;

import lombok.Data;

/*  COFINS = Contribuição para o Financiamento da Seguridade Social
    É um tributo federal que incide sobre o faturamento da empresa.
    Ela é regulada principalmente pela:
    Lei 9.718/1998
    Lei 10.637/2002
    Lei 10.833/2003
*/
@Data
public class COFINSDTO {
    COFINSAliqDTO cofinsAliq;
    COFINSNTDTO cofinsnt;
    COFINSOutrDTO cofinsOutr;
    COFINSQtdeDTO cofinsQtde;
}
