package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.EstabelecimentoResponseDTO;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EstabelecimentoResponseMapper {

    EstabelecimentoResponseDTO toDto(Estabelecimento estabelecimento);
}
