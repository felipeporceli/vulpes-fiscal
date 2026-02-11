package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NfceDTO {

    private IdeDTO ide;
    private EmitDTO emit;
    private DestDTO dest;
    private List<DetDTO> det;

    private AvulsaDTO avulsa;     // sempre null
    private RetiradaDTO retirada; // sempre null
    private EntregaDTO entrega;   // sempre null
    private List<AutXMLDTO> autXML; // sempre null

    // demais grupos...
}
