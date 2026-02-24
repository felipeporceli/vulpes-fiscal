package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroItemVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroPagamentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.ConsumidorResponseDTO;
import com.vulpesfiscal.demo.controllers.dtos.VendaResponseDTO;
import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.ItemVenda;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Venda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-20T21:17:35-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
)
@Component
public class VendaMapperImpl implements VendaMapper {

    @Override
    public VendaResponseDTO toResponseDTO(Venda venda) {
        if ( venda == null ) {
            return null;
        }

        Integer empresaId = null;
        Integer id = null;
        LocalDateTime dataCriacao = null;
        Integer criadoPor = null;
        LocalDateTime atualizadoEm = null;
        Integer atualizadoPor = null;
        ConsumidorResponseDTO consumidor = null;

        empresaId = vendaEmpresaId( venda );
        id = venda.getId();
        dataCriacao = venda.getDataCriacao();
        criadoPor = venda.getCriadoPor();
        atualizadoEm = venda.getAtualizadoEm();
        atualizadoPor = venda.getAtualizadoPor();
        consumidor = consumidorToConsumidorResponseDTO( venda.getConsumidor() );

        BigDecimal desconto = null;

        VendaResponseDTO vendaResponseDTO = new VendaResponseDTO( id, dataCriacao, criadoPor, atualizadoEm, atualizadoPor, desconto, empresaId, consumidor );

        return vendaResponseDTO;
    }

    @Override
    public Venda toEntity(CadastroVendaDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Venda venda = new Venda();

        venda.setPagamento( cadastroPagamentoDTOToPagamento( dto.pagamento() ) );
        venda.setItens( cadastroItemVendaDTOListToItemVendaList( dto.itens() ) );
        venda.setEmitirNfce( dto.emitirNfce() );

        return venda;
    }

    private Integer vendaEmpresaId(Venda venda) {
        Empresa empresa = venda.getEmpresa();
        if ( empresa == null ) {
            return null;
        }
        return empresa.getId();
    }

    protected ConsumidorResponseDTO consumidorToConsumidorResponseDTO(Consumidor consumidor) {
        if ( consumidor == null ) {
            return null;
        }

        Integer id = null;
        String nome = null;
        String cpf = null;
        String email = null;

        id = consumidor.getId();
        nome = consumidor.getNome();
        cpf = consumidor.getCpf();
        email = consumidor.getEmail();

        ConsumidorResponseDTO consumidorResponseDTO = new ConsumidorResponseDTO( id, nome, cpf, email );

        return consumidorResponseDTO;
    }

    protected Pagamento cadastroPagamentoDTOToPagamento(CadastroPagamentoDTO cadastroPagamentoDTO) {
        if ( cadastroPagamentoDTO == null ) {
            return null;
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setMetodoPagamento( cadastroPagamentoDTO.metodoPagamento() );
        pagamento.setValor( cadastroPagamentoDTO.valor() );
        pagamento.setValorRecebido( cadastroPagamentoDTO.valorRecebido() );
        pagamento.setDesconto( cadastroPagamentoDTO.desconto() );
        pagamento.setParcelas( cadastroPagamentoDTO.parcelas() );
        pagamento.setStatusPagamento( cadastroPagamentoDTO.statusPagamento() );

        return pagamento;
    }

    protected ItemVenda cadastroItemVendaDTOToItemVenda(CadastroItemVendaDTO cadastroItemVendaDTO) {
        if ( cadastroItemVendaDTO == null ) {
            return null;
        }

        ItemVenda itemVenda = new ItemVenda();

        if ( cadastroItemVendaDTO.quantidade() != null ) {
            itemVenda.setQuantidade( BigDecimal.valueOf( cadastroItemVendaDTO.quantidade() ) );
        }
        itemVenda.setCfop( cadastroItemVendaDTO.cfop() );

        return itemVenda;
    }

    protected List<ItemVenda> cadastroItemVendaDTOListToItemVendaList(List<CadastroItemVendaDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<ItemVenda> list1 = new ArrayList<ItemVenda>( list.size() );
        for ( CadastroItemVendaDTO cadastroItemVendaDTO : list ) {
            list1.add( cadastroItemVendaDTOToItemVenda( cadastroItemVendaDTO ) );
        }

        return list1;
    }
}
