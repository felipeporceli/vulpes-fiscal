package com.vulpesfiscal.demo.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = false)

public record AtualizacaoPagamentoDTO(

        StatusPagamento statusPagamento
){
}
