package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.VendaResponseDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.NfceDTO;
import com.vulpesfiscal.demo.entities.Nfce;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.entities.Venda;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface NfceMapper {
    Nfce toEntity(NfceDTO dto);
}

