package com.vulpesfiscal.demo.controllers.dtos.focusnfe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FocusNfceRequestDTO {

    // Emitente
    private String cnpjEmitente;

    // Identificação
    private String dataEmissao;
    private String naturezaOperacao;
    private String modalidadeFrete;
    private String localDestino;
    private String presencaComprador;

    // Destinatário (opcional em NFC-e)
    private String nomeDestinatario;
    private String cpfDestinatario;
    private String cnpjDestinatario;
    private String indicadorInscricaoEstadualDestinatario;

    // Informações adicionais
    private String informacoesAdicionaisContribuinte;

    // Itens e pagamento
    private List<FocusNfceItemDTO> items;
    private List<FocusNfceFormaPagamentoDTO> formasPagamento;
}
