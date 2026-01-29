package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConsumidorMapper {

    ResultadoPesquisaConsumidorDTO toDTO(Consumidor consumidor);

    public abstract Consumidor toEntity(CadastroConsumidorDTO dto);

    public abstract Consumidor toEntityUpdate(AtualizacaoConsumidorDTO dto, @MappingTarget Consumidor consumidor);

    ConsumidorResponseDTO toResponseDTO(Consumidor consumidor);
}