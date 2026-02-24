package com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ibscbs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GCredPresIBSZFMDTO {

    private String competApur;
    private Integer tpCredPresIBSZFM;
    private BigDecimal vCredPresIBSZFM;

}
