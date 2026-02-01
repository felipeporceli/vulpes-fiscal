package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaProdutoDTO;
import com.vulpesfiscal.demo.entities.Produto;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T09:36:53-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
)
@Component
public class ProdutoMapperImpl implements ProdutoMapper {

    @Override
    public ResultadoPesquisaProdutoDTO toDTO(Produto produto) {
        if ( produto == null ) {
            return null;
        }

        String descricao = null;
        String codigoBarras = null;
        Integer idProduto = null;
        Integer ncm = null;
        Integer cfop = null;
        String unidade = null;
        BigDecimal preco = null;
        boolean ativo = false;
        Integer qtdEstoque = null;

        descricao = produto.getDescricao();
        codigoBarras = produto.getCodigoBarras();
        idProduto = produto.getIdProduto();
        ncm = produto.getNcm();
        cfop = produto.getCfop();
        unidade = produto.getUnidade();
        preco = produto.getPreco();
        ativo = produto.isAtivo();
        qtdEstoque = produto.getQtdEstoque();

        ResultadoPesquisaProdutoDTO resultadoPesquisaProdutoDTO = new ResultadoPesquisaProdutoDTO( descricao, codigoBarras, idProduto, ncm, cfop, unidade, preco, ativo, qtdEstoque );

        return resultadoPesquisaProdutoDTO;
    }

    @Override
    public Produto toEntity(CadastroProdutoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Produto produto = new Produto();

        produto.setIdProduto( dto.idProduto() );
        produto.setDescricao( dto.descricao() );
        produto.setCodigoBarras( dto.codigoBarras() );
        produto.setQtdEstoque( dto.qtdEstoque() );
        produto.setNcm( dto.ncm() );
        produto.setCfop( dto.cfop() );
        produto.setUnidade( dto.unidade() );
        if ( dto.preco() != null ) {
            produto.setPreco( BigDecimal.valueOf( dto.preco() ) );
        }
        if ( dto.ativo() != null ) {
            produto.setAtivo( dto.ativo() );
        }

        return produto;
    }

    @Override
    public Produto toEntityUpdate(AtualizacaoProdutoDTO dto, Produto produto) {
        if ( dto == null ) {
            return produto;
        }

        produto.setDescricao( dto.descricao() );
        produto.setQtdEstoque( dto.qtdEstoque() );
        produto.setNcm( dto.ncm() );
        produto.setCfop( dto.cfop() );
        produto.setUnidade( dto.unidade() );
        produto.setPreco( dto.preco() );
        produto.setAtivo( dto.ativo() );

        return produto;
    }
}
