package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoTributacaoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaProdutoDTO;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.entities.ProdutoTributacao;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProdutoTributacaoMapper {

    ResultadoPesquisaProdutoDTO toDTO(Produto produto);

    ProdutoTributacao toEntity(CadastroProdutoTributacaoDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Produto toEntityUpdate(AtualizacaoProdutoDTO dto, @MappingTarget Produto produto);
}
