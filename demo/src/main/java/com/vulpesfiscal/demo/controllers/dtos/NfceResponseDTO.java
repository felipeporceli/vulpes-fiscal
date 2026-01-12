package com.vulpesfiscal.demo.controllers.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record NfceResponseDTO (
         Integer id,
         Integer numero,
         Integer serie,
         String chaveAcesso,
         LocalDateTime dataEmissao,
         BigDecimal valorTotal,
         String status,
         EmpresaResponseDTO empresa,
         EstabelecimentoResponseDTO estabelecimento,
         List<ItemNfceResponseDTO> itens) {
}
