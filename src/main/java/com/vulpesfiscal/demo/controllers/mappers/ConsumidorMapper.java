package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ConsumidorMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    ResultadoPesquisaConsumidorDTO toDTO(Consumidor consumidor);

    Consumidor toEntity(CadastroConsumidorDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Consumidor toEntityUpdate(AtualizacaoConsumidorDTO dto, @MappingTarget Consumidor consumidor);

    ConsumidorResponseDTO toResponseDTO(Consumidor consumidor);
}