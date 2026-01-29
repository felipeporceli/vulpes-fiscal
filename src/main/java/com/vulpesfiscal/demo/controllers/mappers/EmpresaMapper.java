package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.entities.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    ResultadoPesquisaEmpresaDTO toDTO(Empresa empresa);

    public abstract Empresa toEntity(CadastroEmpresaDTO dto);

    public abstract Empresa toEntityUpdate(AtualizacaoEmpresaDTO dto, @MappingTarget Empresa empresa);

}

