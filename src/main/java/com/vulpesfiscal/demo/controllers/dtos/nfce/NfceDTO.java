package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.DetDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ImpostoDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.TotalDTO;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NfceDTO {

    String versao;
    String id;
    private IdeDTO ide;
    private EmitDTO emit;
    private DestDTO dest;
    private List<DetDTO> det;
    private ImpostoDTO imposto;
    private TotalDTO total;

    private AvulsaDTO avulsa;     // sempre null
    private RetiradaDTO retirada; // sempre null
    private EntregaDTO entrega;   // sempre null
    private List<AutXMLDTO> autXML; // sempre null

    // demais grupos...
}
