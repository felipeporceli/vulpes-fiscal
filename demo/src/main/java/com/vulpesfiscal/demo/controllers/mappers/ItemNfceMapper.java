package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroItemNfceDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaProdutoDTO;
import com.vulpesfiscal.demo.entities.ItemNfce;
import com.vulpesfiscal.demo.entities.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemNfceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nfce", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "valorTotal", ignore = true)
    ItemNfce toEntity(CadastroItemNfceDTO dto);
}


