package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins;

import lombok.Data;

@Data
public class COFINSDTO {
    COFINSAliqDTO cofinsAliq;
    COFINSNTDTO cofinsnt;
    COFINSOutrDTO cofinsOutr;
    COFINSQtdeDTO cofinsQtde;
}
