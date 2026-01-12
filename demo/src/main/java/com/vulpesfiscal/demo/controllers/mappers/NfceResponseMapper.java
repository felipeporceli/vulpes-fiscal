package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.NfceResponseDTO;
import com.vulpesfiscal.demo.entities.Nfce;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {
                EmpresaResponseMapper.class,
                EstabelecimentoResponseMapper.class,
                ItemNfceResponseMapper.class
        }
)
public interface NfceResponseMapper {

    NfceResponseDTO toDto(Nfce nfce);
}

