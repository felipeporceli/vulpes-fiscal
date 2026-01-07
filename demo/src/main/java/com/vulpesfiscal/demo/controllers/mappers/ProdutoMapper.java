package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaProdutoDTO;
import com.vulpesfiscal.demo.entities.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ResultadoPesquisaProdutoDTO toDTO(Produto produto);

    @Mapping(target = "idTecnico", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(source = "idProduto", target = "idProduto")
    Produto toEntity(CadastroProdutoDTO dto);

    Produto toEntityUpdate(AtualizacaoProdutoDTO dto, @MappingTarget Produto produto);
}
