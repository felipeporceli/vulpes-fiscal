package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.CadastroVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.VendaResponseDTO;
import com.vulpesfiscal.demo.entities.Venda;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface VendaMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    VendaResponseDTO toResponseDTO(Venda venda);

    @Mapping(source = "empresa.id",           target = "empresaId")
    @Mapping(source = "estabelecimento.id",   target = "estabelecimentoId")
    @Mapping(source = "consumidor.id",        target = "consumidorId")
    @Mapping(source = "consumidor.nome",      target = "consumidorNome")
    @Mapping(source = "pagamento.metodoPagamento", target = "metodoPagamento")
    @Mapping(source = "pagamento.statusPagamento", target = "statusPagamento")
    @Mapping(source = "pagamento.valorFinal", target = "valorFinal")
    @Mapping(source = "pagamento.desconto",   target = "desconto")
    ResultadoPesquisaVendaDTO toDTO(Venda venda);

    Venda toEntity(CadastroVendaDTO dto);

}

