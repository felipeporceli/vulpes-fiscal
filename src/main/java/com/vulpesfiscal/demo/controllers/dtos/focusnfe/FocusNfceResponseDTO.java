package com.vulpesfiscal.demo.controllers.dtos.focusnfe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FocusNfceResponseDTO {

    /** autorizado | processando_autorizacao | cancelado | erro_autorizacao */
    private String status;

    private String chaveNfce;
    private String numero;
    private String serie;
    private String protocoloNota;
    private String caminhoXmlNotaFiscal;
    private String caminhoDanfe;

    // Campos de erro
    private String codigo;
    private String mensagem;
    private List<String> erros;
}
