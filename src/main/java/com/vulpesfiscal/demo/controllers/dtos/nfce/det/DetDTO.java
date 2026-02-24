package com.vulpesfiscal.demo.controllers.dtos.nfce.det;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vulpesfiscal.demo.controllers.dtos.nfce.ProdDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.dfereferenciado.DfeReferenciadoDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ImpostoDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.impostodevol.ImpostoDevolDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.obsitem.ObsItemDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetDTO {

    private Integer nItem;
    private ProdDTO prod;
    //private ImpostoDTO imposto; // vem depois
    private String infAdProd;
    private ImpostoDTO imposto;
    private ImpostoDevolDTO impostoDevol;
    private ObsItemDTO obsItem;
    private BigDecimal vItem;
    private DfeReferenciadoDTO dfeReferenciado;
}

