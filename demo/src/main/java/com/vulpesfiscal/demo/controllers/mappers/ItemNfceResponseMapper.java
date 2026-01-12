package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.ItemNfceResponseDTO;
import com.vulpesfiscal.demo.entities.ItemNfce;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemNfceResponseMapper {

    @Mapping(target = "produtoId", source = "produto.idTecnico")
    ItemNfceResponseDTO toDto(ItemNfce item);
}

