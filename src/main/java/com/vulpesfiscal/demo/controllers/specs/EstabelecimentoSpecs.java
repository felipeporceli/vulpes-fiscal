package com.vulpesfiscal.demo.controllers.specs;

import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import org.springframework.data.jpa.domain.Specification;

public class EstabelecimentoSpecs {

    // SELECT * FROM Estabelecimento WHERE cnpj = :cnpj
    public static Specification<Estabelecimento> cnpjIgual (String cnpj) {
        return (root, query, cb) -> cb.equal(root.get("cnpj"), cnpj);
    }

    // SELECT * FROM Estabelecimento WHERE UPPER (nomeFantasia) LIKE '%nomeFantasia.toUpperCase%'
    public static Specification<Estabelecimento> nomeFantasiaLike (String nomeFantasia) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("nomeFantasia")), "%" + nomeFantasia.toUpperCase() + "%" );
    }

    // SELECT * FROM Estabelecimento WHERE UPPER (cidade) LIKE '%cidade.toUpperCase%'
    public static Specification<Estabelecimento> cidadeLike (String cidade) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("cidade")), "%" + cidade.toUpperCase() + "%" );
    }

    // SELECT * FROM Estabelecimento WHERE estado = :estado
    public static Specification<Estabelecimento> estadoIgual (String estado) {
        return (root, query, cb) -> cb.equal(root.get("estado"), estado);
    }

    // SELECT * FROM Estabelecimento WHERE matriz = : matriz
    public static Specification<Estabelecimento> matrizIgual (Boolean matriz) {
        return (root, query, cb) -> cb.equal(root.get("matriz"), matriz);
    }

    // SELECT * FROM estabelecimento WHERE status = :status
    public static Specification<Estabelecimento> statusIgual (StatusEmpresa status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

}
