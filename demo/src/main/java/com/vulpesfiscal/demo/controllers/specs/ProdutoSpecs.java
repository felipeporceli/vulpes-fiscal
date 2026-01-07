package com.vulpesfiscal.demo.controllers.specs;

import com.vulpesfiscal.demo.entities.Produto;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

// root - Representa a entidade que está sendo consultada, no caso "Produto".
// query - Representa a consulta que está sendo construída, mas não é usada nesse caso.
// cb - CriteriaBuilder, que cria as condições (where) dinamicamente.

// Classe para criação de métodos Specifications para melhor filtragem na pesquisa.
public class ProdutoSpecs {

    // SELECT * FROM produto WHERE id = :id
    public static Specification<Produto> idProdutoIgual (Integer idProduto) {
        return (root, query, cb) -> cb.equal(root.get("idProduto"), idProduto);
    }

    // SELECT * FROM produto WHERE ncm = :ncm
    public static Specification<Produto> ncmIgual (Integer ncm) {
        return (root, query, cb) -> cb.equal(root.get("ncm"), ncm);
    }

    // SELECT * FROM produto WHERE UPPER (descricao) LIKE '%descricao.toUpperCase%'
    public static Specification<Produto> descricaoLike (String descricao) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("descricao")), "%" + descricao.toUpperCase() + "%" );
    }

    // SELECT * FROM produto WHERE UPPER (codigoBarras) LIKE '%codigoBarras.toUpperCase%'
    public static Specification<Produto> codigoBarrasLike (String codigoBarras) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("codigoBarras")), "%" + codigoBarras.toUpperCase() + "%" );
    }

    // SELECT * FROM produto WHERE preco BETWEEN :precoMin AND precoMax;
    public static Specification<Produto> precoEntre(BigDecimal precoMin, BigDecimal precoMax) {
        return (root, query, cb) -> {
            if (precoMin != null && precoMax != null) {
                return cb.between(root.get("preco"), precoMin, precoMax);
            }

            if (precoMin != null) {
                return cb.greaterThanOrEqualTo(root.get("preco"), precoMin);
            }

            if (precoMax != null) {
                return cb.lessThanOrEqualTo(root.get("preco"), precoMax);
            }

            return cb.conjunction(); // não aplica filtro
        };
    }


    // SELECT * FROM Produto WHERE ativo = : ativo
    public static Specification<Produto> ativoIgual (boolean ativo) {
        return (root, query, cb) -> cb.equal(root.get("ativo"), ativo);
    }

    public static Specification<Produto> qtdEstoqueEntre(Integer qtdMin, Integer qtdMax) {
        return (root, query, cb) -> {
            if (qtdMin != null && qtdMax != null) {
                return cb.between(root.get("qtdEstoque"), qtdMin, qtdMax);
            }

            if (qtdMin != null) {
                return cb.greaterThanOrEqualTo(root.get("qtdEstoque"), qtdMin);
            }

            if (qtdMax != null) {
                return cb.lessThanOrEqualTo(root.get("qtdEstoque"), qtdMax);
            }

            return cb.conjunction(); // não aplica filtro
        };
    }

    // SELECT * FROM produto WHERE empresa_id = :empresaId
    public static Specification<Produto> empresaIdIgual(Integer empresaId) {
        return (root, query, cb) ->
                cb.equal(root.get("empresa").get("id"), empresaId);
    }



}
