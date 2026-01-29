package com.vulpesfiscal.demo.controllers.mappers;

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
}

