package com.vulpesfiscal.demo.controllers.specs;

import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class PagamentoSpecs {

    // SELECT * FROM pagamento WHERE id = :id
    public static Specification<Pagamento> idIgual (Integer id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    // SELECT * FROM pagamento WHERE UPPER (metodoPagamento) LIKE '%metodoPagamento.toUpperCase%'
    public static Specification<Pagamento> metodoPagamentoLike (MetodoPagamento metodoPagamento) {
        return (root, query, cb) -> cb.equal(root.get("metodoPagamento"), metodoPagamento);
    }


    // SELECT * FROM pagamento WHERE preco BETWEEN :valorMin AND valorMax;
    public static Specification<Pagamento> valorEntre(BigDecimal valorMin, BigDecimal valorMax) {
        return (root, query, cb) -> {
            if (valorMin != null && valorMax != null) {
                return cb.between(root.get("valor"), valorMin, valorMax);
            }

            if (valorMin != null) {
                return cb.greaterThanOrEqualTo(root.get("valor"), valorMin);
            }

            if (valorMax != null) {
                return cb.lessThanOrEqualTo(root.get("valor"), valorMax);
            }

            return cb.conjunction();
        };
    }


    // SELECT * FROM pagamento WHERE UPPER (statusPagamento) LIKE '%statusPagamento.toUpperCase%'
    public static Specification<Pagamento> statusPagamentoIgual (StatusPagamento statusPagamento) {
        return (root, query, cb) -> cb.equal(root.get("statusPagamento"), statusPagamento);

    }


    // SELECT * FROM pagamento WHERE idEmpresa = :idEmpresa
    public static Specification<Pagamento> empresaIdIgual (Integer empresaId) {
        return (root, query, cb) ->
                cb.equal(root.get("empresa").get("id"), empresaId);
    }

    // SELECT * FROM pagamento WHERE idEstabelecimento = :idEstabelecimento
    public static Specification<Pagamento> estabelecimentoIdIgual (Integer estabelecimentoId) {
        return (root, query, cb) -> cb.equal(root.get("estabelecimento").get("id"), estabelecimentoId);
    }

}
