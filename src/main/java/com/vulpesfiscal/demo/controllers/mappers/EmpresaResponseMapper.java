package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.EmpresaResponseDTO;
import com.vulpesfiscal.demo.entities.Empresa;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmpresaResponseMapper {

    EmpresaResponseDTO toDto(Empresa empresa);
}

