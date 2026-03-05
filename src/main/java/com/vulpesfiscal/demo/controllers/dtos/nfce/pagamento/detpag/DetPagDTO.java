package com.vulpesfiscal.demo.controllers.dtos.nfce.pagamento.detpag;

import com.vulpesfiscal.demo.controllers.dtos.nfce.pagamento.card.CardDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DetPagDTO {

    /**
     * Indicador da Forma de Pagamento
     * 0 - À vista
     * 1 - A prazo
     */
    private Integer indPag;

    /**
     * Forma de pagamento (obrigatório)
     * Exemplos:
     * 01 - Dinheiro
     * 03 - Cartão de crédito
     * 04 - Cartão de débito
     * 17 - PIX
     * 90 - Sem pagamento
     */
    private String tPag;

    /**
     * Descrição do meio de pagamento
     */
    private String xPag;

    /**
     * Valor do pagamento
     * Obrigatório exceto quando tPag = 90
     */
    private BigDecimal vPag;

    /**
     * Data do pagamento
     */
    private LocalDate dPag;

    /**
     * CNPJ do estabelecimento que processou o pagamento
     */
    private String CNPJPag;

    /**
     * UF do estabelecimento do pagamento
     */
    private String UFPag;

    /**
     * Dados de pagamento eletrônico (cartão, PIX, etc.)
     */
    private CardDTO card;

    // getters e setters
}
