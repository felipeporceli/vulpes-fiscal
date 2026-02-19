package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetDTO {

    private Integer nItem;
    private ProdDTO prod;
    //private ImpostoDTO imposto; // vem depois
    private String infAdProd;
    private ImpostoDTO imposto;
}

