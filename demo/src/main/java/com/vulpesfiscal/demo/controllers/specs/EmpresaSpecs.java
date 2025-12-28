package com.vulpesfiscal.demo.controllers.specs;

import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import org.springframework.data.jpa.domain.Specification;

// root - Representa a entidade que está sendo consultada, no caso "Empresa".
// query - Representa a consulta que está sendo construída, mas não é usada nesse caso.
// cb - CriteriaBuilder, que cria as condições (where) dinamicamente.

// Classe para criação de métodos Specifications para melhor filtragem na pesquisa.
public class EmpresaSpecs {

    // SELECT * FROM empresa WHERE cnpj = :cnpj
    public static Specification<Empresa> cnpjIgual (String cnpj) {
        return (root, query, cb) -> cb.equal(root.get("cnpj"), cnpj);
    }

    // SELECT * FROM empresa WHERE UPPER (razaoSocial) LIKE '%razaoSocial.toUpperCase%'
    public static Specification<Empresa> razaoSocialLike (String razaoSocial) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("razaoSocial")), "%" + razaoSocial.toUpperCase() + "%" );
    }

    // SELECT * FROM empresa WHERE inscricaoEstadual = :inscricaoEstadual
    public static Specification<Empresa> inscricaoIgual (String inscricaoEstadual) {
        return (root, query, cb) -> cb.equal(root.get("inscricaoEstadual"), inscricaoEstadual);
    }

    // SELECT * FROM empresa WHERE regimeTributario = : regimeTributario
    public static Specification<Empresa> regimeTributarioIgual (String regimeTributario) {
        return (root, query, cb) -> cb.equal(root.get("regimeTributario"), regimeTributario);
    }

    // SELECT * FROM empresa WHERE status = :status
    public static Specification<Empresa> statusIgual (StatusEmpresa statusEmpresa) {
        return (root, query, cb) -> cb.equal(root.get("statusEmpresa"), statusEmpresa);
    }

}
