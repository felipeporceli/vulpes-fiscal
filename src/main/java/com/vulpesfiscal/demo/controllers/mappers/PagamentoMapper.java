package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Produto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {

    @Mapping(source = "statusPagamento", target = "statusPagamento")
    @Mapping(source = "valorRecebido", target = "valorRecebido")
    Pagamento toEntity(CadastroPagamentoDTO dto);

    ResultadoPesquisaPagamentoDTO toDTO(Pagamento pagamento);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Pagamento toEntityUpdate(AtualizacaoPagamentoDTO dto, @MappingTarget Pagamento pagamento);


}
