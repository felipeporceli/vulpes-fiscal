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
    date = "2026-04-11T17:49:30-0300",
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
        if ( produto.getNcm() != null ) {
            ncm = Integer.parseInt( produto.getNcm() );
        }
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
        produto.setOrig( dto.orig() );
        produto.setDescricao( dto.descricao() );
        produto.setCodigoBarras( dto.codigoBarras() );
        produto.setQtdEstoque( dto.qtdEstoque() );
        if ( dto.ncm() != null ) {
            produto.setNcm( String.valueOf( dto.ncm() ) );
        }
        produto.setCfop( dto.cfop() );
        produto.setUnidade( dto.unidade() );
        if ( dto.preco() != null ) {
            produto.setPreco( BigDecimal.valueOf( dto.preco() ) );
        }
        if ( dto.ativo() != null ) {
            produto.setAtivo( dto.ativo() );
        }
        produto.setCest( dto.cest() );

        return produto;
    }

    @Override
    public Produto toEntityUpdate(AtualizacaoProdutoDTO dto, Produto produto) {
        if ( dto == null ) {
            return produto;
        }

        if ( dto.descricao() != null ) {
            produto.setDescricao( dto.descricao() );
        }
        if ( dto.codigoBarras() != null ) {
            produto.setCodigoBarras( dto.codigoBarras() );
        }
        if ( dto.qtdEstoque() != null ) {
            produto.setQtdEstoque( dto.qtdEstoque() );
        }
        if ( dto.ncm() != null ) {
            produto.setNcm( dto.ncm() );
        }
        if ( dto.cfop() != null ) {
            produto.setCfop( dto.cfop() );
        }
        if ( dto.unidade() != null ) {
            produto.setUnidade( dto.unidade() );
        }
        if ( dto.preco() != null ) {
            produto.setPreco( dto.preco() );
        }
        if ( dto.ativo() != null ) {
            produto.setAtivo( dto.ativo() );
        }
        if ( dto.cest() != null ) {
            produto.setCest( dto.cest() );
        }
        if ( dto.orig() != null ) {
            produto.setOrig( dto.orig() );
        }

        return produto;
    }
}
