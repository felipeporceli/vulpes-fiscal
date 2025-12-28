package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEmpresaDTO;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    ResultadoPesquisaEmpresaDTO toDTO(Empresa empresa);
}

