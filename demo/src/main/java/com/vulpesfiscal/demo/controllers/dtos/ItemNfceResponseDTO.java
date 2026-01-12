package com.vulpesfiscal.demo.controllers.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ItemNfceResponseDTO (
         Integer produtoId,
         BigDecimal quantidade,
         BigDecimal valorUnitario,
         BigDecimal valorTotal,
         String ncm){
}
