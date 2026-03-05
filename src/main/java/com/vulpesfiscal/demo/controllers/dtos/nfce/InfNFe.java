package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.DetDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ImpostoDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.pagamento.PagamentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.TotalDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.transporte.TransporteDTO;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfNFe {

    String versao;
    String id;
    private IdeDTO ide;
    private EmitDTO emit;
    private DestDTO dest;
    private List<DetDTO> det;
    private ImpostoDTO imposto;
    private TotalDTO total;
    private TransporteDTO transporte;
    private PagamentoDTO pagamento;

    private AvulsaDTO avulsa;     // sempre null
    private RetiradaDTO retirada; // sempre null
    private EntregaDTO entrega;   // sempre null
    private List<AutXMLDTO> autXML; // sempre null

    // demais grupos...
}
