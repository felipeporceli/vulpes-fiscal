package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroItemVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroPagamentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.ConsumidorResponseDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.VendaResponseDTO;
import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.ItemVenda;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Venda;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-21T18:45:17-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
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
        LocalDateTime atualizadoEm = null;
        Integer atualizadoPor = null;
        ConsumidorResponseDTO consumidor = null;

        empresaId = vendaEmpresaId( venda );
        id = venda.getId();
        dataCriacao = venda.getDataCriacao();
        atualizadoEm = venda.getAtualizadoEm();
        atualizadoPor = venda.getAtualizadoPor();
        consumidor = consumidorToConsumidorResponseDTO( venda.getConsumidor() );

        Integer criadoPor = null;
        BigDecimal desconto = null;

        VendaResponseDTO vendaResponseDTO = new VendaResponseDTO( id, dataCriacao, criadoPor, atualizadoEm, atualizadoPor, desconto, empresaId, consumidor );

        return vendaResponseDTO;
    }

    @Override
    public ResultadoPesquisaVendaDTO toDTO(Venda venda) {
        if ( venda == null ) {
            return null;
        }

        Integer empresaId = null;
        Integer estabelecimentoId = null;
        Integer consumidorId = null;
        String consumidorNome = null;
        MetodoPagamento metodoPagamento = null;
        StatusPagamento statusPagamento = null;
        BigDecimal valorFinal = null;
        BigDecimal desconto = null;
        Integer id = null;
        BigDecimal valorTotal = null;
        Integer parcelas = null;
        LocalDateTime dataCriacao = null;

        empresaId = vendaEmpresaId( venda );
        estabelecimentoId = vendaEstabelecimentoId( venda );
        consumidorId = vendaConsumidorId( venda );
        consumidorNome = vendaConsumidorNome( venda );
        metodoPagamento = vendaPagamentoMetodoPagamento( venda );
        statusPagamento = vendaPagamentoStatusPagamento( venda );
        valorFinal = vendaPagamentoValorFinal( venda );
        desconto = vendaPagamentoDesconto( venda );
        id = venda.getId();
        valorTotal = venda.getValorTotal();
        parcelas = venda.getParcelas();
        dataCriacao = venda.getDataCriacao();

        Integer vendedorId = null;
        String vendedorNome = null;
        BigDecimal valorRecebido = null;
        String usuarioNome = null;

        ResultadoPesquisaVendaDTO resultadoPesquisaVendaDTO = new ResultadoPesquisaVendaDTO( id, empresaId, estabelecimentoId, valorTotal, parcelas, dataCriacao, consumidorId, consumidorNome, metodoPagamento, statusPagamento, valorFinal, desconto, vendedorId, vendedorNome, valorRecebido, usuarioNome );

        return resultadoPesquisaVendaDTO;
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

    private Integer vendaEstabelecimentoId(Venda venda) {
        Estabelecimento estabelecimento = venda.getEstabelecimento();
        if ( estabelecimento == null ) {
            return null;
        }
        return estabelecimento.getId();
    }

    private Integer vendaConsumidorId(Venda venda) {
        Consumidor consumidor = venda.getConsumidor();
        if ( consumidor == null ) {
            return null;
        }
        return consumidor.getId();
    }

    private String vendaConsumidorNome(Venda venda) {
        Consumidor consumidor = venda.getConsumidor();
        if ( consumidor == null ) {
            return null;
        }
        return consumidor.getNome();
    }

    private MetodoPagamento vendaPagamentoMetodoPagamento(Venda venda) {
        Pagamento pagamento = venda.getPagamento();
        if ( pagamento == null ) {
            return null;
        }
        return pagamento.getMetodoPagamento();
    }

    private StatusPagamento vendaPagamentoStatusPagamento(Venda venda) {
        Pagamento pagamento = venda.getPagamento();
        if ( pagamento == null ) {
            return null;
        }
        return pagamento.getStatusPagamento();
    }

    private BigDecimal vendaPagamentoValorFinal(Venda venda) {
        Pagamento pagamento = venda.getPagamento();
        if ( pagamento == null ) {
            return null;
        }
        return pagamento.getValorFinal();
    }

    private BigDecimal vendaPagamentoDesconto(Venda venda) {
        Pagamento pagamento = venda.getPagamento();
        if ( pagamento == null ) {
            return null;
        }
        return pagamento.getDesconto();
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
