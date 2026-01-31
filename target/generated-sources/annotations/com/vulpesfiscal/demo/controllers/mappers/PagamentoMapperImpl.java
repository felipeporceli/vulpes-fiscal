package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroPagamentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaPagamentoDTO;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-29T06:41:37-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
)
@Component
public class PagamentoMapperImpl implements PagamentoMapper {

    @Override
    public Pagamento toEntity(CadastroPagamentoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setMetodoPagamento( dto.metodoPagamento() );
        pagamento.setValor( dto.valor() );
        pagamento.setTroco( dto.troco() );
        pagamento.setParcelas( dto.parcelas() );
        pagamento.setStatusPagamento( dto.statusPagamento() );

        return pagamento;
    }

    @Override
    public ResultadoPesquisaPagamentoDTO toDTO(Pagamento pagamento) {
        if ( pagamento == null ) {
            return null;
        }

        MetodoPagamento metodoPagamento = null;
        BigDecimal valor = null;
        BigDecimal troco = null;
        Integer parcelas = null;
        StatusPagamento statusPagamento = null;

        metodoPagamento = pagamento.getMetodoPagamento();
        valor = pagamento.getValor();
        troco = pagamento.getTroco();
        parcelas = pagamento.getParcelas();
        statusPagamento = pagamento.getStatusPagamento();

        Integer empresaId = null;
        Integer estabelecimentoId = null;
        Timestamp pagoEm = null;

        ResultadoPesquisaPagamentoDTO resultadoPesquisaPagamentoDTO = new ResultadoPesquisaPagamentoDTO( metodoPagamento, valor, troco, parcelas, statusPagamento, empresaId, estabelecimentoId, pagoEm );

        return resultadoPesquisaPagamentoDTO;
    }
}
