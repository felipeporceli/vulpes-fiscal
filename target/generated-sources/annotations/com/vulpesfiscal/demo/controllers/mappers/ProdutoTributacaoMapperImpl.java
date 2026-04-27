package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoProdutoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroProdutoTributacaoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaProdutoDTO;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.entities.ProdutoTributacao;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-26T22:16:44-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class ProdutoTributacaoMapperImpl implements ProdutoTributacaoMapper {

    @Override
    public ResultadoPesquisaProdutoDTO toDTO(Produto produto) {
        if ( produto == null ) {
            return null;
        }

        Integer idProduto = null;
        String descricao = null;
        String codigoBarras = null;
        Integer ncm = null;
        Integer cfop = null;
        String unidade = null;
        BigDecimal preco = null;
        boolean ativo = false;
        Integer qtdEstoque = null;
        String cest = null;
        Integer orig = null;

        idProduto = produto.getIdProduto();
        descricao = produto.getDescricao();
        codigoBarras = produto.getCodigoBarras();
        if ( produto.getNcm() != null ) {
            ncm = Integer.parseInt( produto.getNcm() );
        }
        cfop = produto.getCfop();
        unidade = produto.getUnidade();
        preco = produto.getPreco();
        ativo = produto.isAtivo();
        qtdEstoque = produto.getQtdEstoque();
        cest = produto.getCest();
        orig = produto.getOrig();

        Integer empresaId = null;

        ResultadoPesquisaProdutoDTO resultadoPesquisaProdutoDTO = new ResultadoPesquisaProdutoDTO( empresaId, idProduto, descricao, codigoBarras, ncm, cfop, unidade, preco, ativo, qtdEstoque, cest, orig );

        return resultadoPesquisaProdutoDTO;
    }

    @Override
    public ProdutoTributacao toEntity(CadastroProdutoTributacaoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ProdutoTributacao produtoTributacao = new ProdutoTributacao();

        produtoTributacao.setNome( dto.getNome() );
        produtoTributacao.setUf( dto.getUf() );
        produtoTributacao.setCfop( dto.getCfop() );
        produtoTributacao.setCstIcms( dto.getCstIcms() );
        produtoTributacao.setCsosnIcms( dto.getCsosnIcms() );
        produtoTributacao.setAliquotaIcms( dto.getAliquotaIcms() );
        produtoTributacao.setPFcp( dto.getPFcp() );
        produtoTributacao.setPRedBc( dto.getPRedBc() );
        produtoTributacao.setTemStAnterior( dto.getTemStAnterior() );
        produtoTributacao.setCstPis( dto.getCstPis() );
        produtoTributacao.setAliquotaPis( dto.getAliquotaPis() );
        produtoTributacao.setCstCofins( dto.getCstCofins() );
        produtoTributacao.setRegimeTributarioEmpresa( dto.getRegimeTributarioEmpresa() );
        produtoTributacao.setAliquotaCofins( dto.getAliquotaCofins() );

        return produtoTributacao;
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
