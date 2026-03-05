package com.vulpesfiscal.demo.controllers.dtos.nfce.pagamento;

import com.vulpesfiscal.demo.controllers.dtos.nfce.pagamento.detpag.DetPagDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagamentoDTO {

    private DetPagDTO detPag;
    private BigDecimal troco;

}
