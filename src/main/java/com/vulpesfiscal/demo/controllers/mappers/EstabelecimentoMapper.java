package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEstabelecimentoDTO;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EstabelecimentoMapper {

    ResultadoPesquisaEstabelecimentoDTO toDTO(Estabelecimento estabelecimento);

    Estabelecimento toEntity(CadastroEstabelecimentoDTO dto);

    public abstract Estabelecimento toEntityUpdate(AtualizacaoEstabelecimentoDTO dto, @MappingTarget Estabelecimento estabelecimento);

}
