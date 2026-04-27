package com.vulpesfiscal.demo.controllers.specs;

import com.vulpesfiscal.demo.entities.Venda;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class VendaSpecs {

    public static Specification<Venda> empresaIdIgual(Integer empresaId) {
        return (root, query, cb) -> cb.equal(root.get("empresa").get("id"), empresaId);
    }

    public static Specification<Venda> estabelecimentoIdIgual(Integer estabelecimentoId) {
        return (root, query, cb) -> cb.equal(root.get("estabelecimento").get("id"), estabelecimentoId);
    }

    public static Specification<Venda> consumidorIdIgual(Integer consumidorId) {
        return (root, query, cb) -> cb.equal(root.get("consumidor").get("id"), consumidorId);
    }

    public static Specification<Venda> dataCriacaoApartirDe(LocalDateTime dataInicio) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dataCriacao"), dataInicio);
    }

    public static Specification<Venda> dataCriacaoAte(LocalDateTime dataFim) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("dataCriacao"), dataFim);
    }
}
