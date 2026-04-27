package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEstabelecimentoDTO;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EstabelecimentoMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    ResultadoPesquisaEstabelecimentoDTO toDTO(Estabelecimento estabelecimento);

    Estabelecimento toEntity(CadastroEstabelecimentoDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Estabelecimento toEntityUpdate(AtualizacaoEstabelecimentoDTO dto, @MappingTarget Estabelecimento estabelecimento);

}
