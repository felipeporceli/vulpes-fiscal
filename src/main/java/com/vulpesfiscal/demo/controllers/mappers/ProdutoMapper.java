package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaProdutoDTO;
import com.vulpesfiscal.demo.entities.Produto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(source = "empresa.id", target = "empresaId")
    ResultadoPesquisaProdutoDTO toDTO(Produto produto);

    @Mapping(target = "idTecnico", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(source = "idProduto", target = "idProduto")
    @Mapping(source = "orig", target = "orig")
    Produto toEntity(CadastroProdutoDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Produto toEntityUpdate(AtualizacaoProdutoDTO dto, @MappingTarget Produto produto);
}
