package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoPagamentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroPagamentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaPagamentoDTO;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-11T17:49:30-0300",
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

        pagamento.setStatusPagamento( dto.statusPagamento() );
        pagamento.setValorRecebido( dto.valorRecebido() );
        pagamento.setMetodoPagamento( dto.metodoPagamento() );
        pagamento.setValor( dto.valor() );
        pagamento.setDesconto( dto.desconto() );
        pagamento.setParcelas( dto.parcelas() );

        return pagamento;
    }

    @Override
    public ResultadoPesquisaPagamentoDTO toDTO(Pagamento pagamento) {
        if ( pagamento == null ) {
            return null;
        }

        MetodoPagamento metodoPagamento = null;
        BigDecimal valor = null;
        BigDecimal valorRecebido = null;
        BigDecimal troco = null;
        BigDecimal desconto = null;
        BigDecimal valorFinal = null;
        Integer parcelas = null;
        StatusPagamento statusPagamento = null;
        LocalDateTime dataCriacao = null;
        LocalDateTime atualizadoEm = null;

        metodoPagamento = pagamento.getMetodoPagamento();
        valor = pagamento.getValor();
        valorRecebido = pagamento.getValorRecebido();
        troco = pagamento.getTroco();
        desconto = pagamento.getDesconto();
        valorFinal = pagamento.getValorFinal();
        parcelas = pagamento.getParcelas();
        statusPagamento = pagamento.getStatusPagamento();
        dataCriacao = pagamento.getDataCriacao();
        atualizadoEm = pagamento.getAtualizadoEm();

        Integer vendaId = null;
        Integer empresaId = null;
        Integer estabelecimentoId = null;
        Integer consumidorId = null;

        ResultadoPesquisaPagamentoDTO resultadoPesquisaPagamentoDTO = new ResultadoPesquisaPagamentoDTO( metodoPagamento, valor, valorRecebido, troco, desconto, valorFinal, parcelas, statusPagamento, vendaId, empresaId, estabelecimentoId, consumidorId, dataCriacao, atualizadoEm );

        return resultadoPesquisaPagamentoDTO;
    }

    @Override
    public Pagamento toEntityUpdate(AtualizacaoPagamentoDTO dto, Pagamento pagamento) {
        if ( dto == null ) {
            return pagamento;
        }

        if ( dto.statusPagamento() != null ) {
            pagamento.setStatusPagamento( dto.statusPagamento() );
        }

        return pagamento;
    }
}
