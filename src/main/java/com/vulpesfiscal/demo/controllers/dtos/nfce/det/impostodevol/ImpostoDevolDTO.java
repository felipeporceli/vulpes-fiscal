package com.vulpesfiscal.demo.controllers.dtos.nfce.det.impostodevol;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.impostodevol.ipi.IPIDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ImpostoDevolDTO {

    private BigDecimal pDevol = null;
    private IPIDTO ipi;

}
