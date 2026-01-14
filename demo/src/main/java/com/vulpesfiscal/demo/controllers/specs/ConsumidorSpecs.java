package com.vulpesfiscal.demo.controllers.specs;

import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import org.springframework.data.jpa.domain.Specification;

// root - Representa a entidade que está sendo consultada, no caso "Empresa".
// query - Representa a consulta que está sendo construída, mas não é usada nesse caso.
// cb - CriteriaBuilder, que cria as condições (where) dinamicamente.

// Classe para criação de métodos Specifications para melhor filtragem na pesquisa.
public class ConsumidorSpecs {

    // SELECT * FROM consumidor WHERE id = :id
    public static Specification<Consumidor> idIgual (Integer id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    // SELECT * FROM consumidor WHERE UPPER (nome) LIKE '%nome.toUpperCase%'
    public static Specification<Consumidor> nomeLike (String nome) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("nome")), "%" + nome.toUpperCase() + "%" );
    }

    // SELECT * FROM consumidor WHERE UPPER (cpf) LIKE '%cpf.toUpperCase%'
    public static Specification<Consumidor> cpfLike (String cpf) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("cpf")), "%" + cpf.toUpperCase() + "%" );
    }

    // SELECT * FROM consumidor WHERE UPPER (email) LIKE '%email.toUpperCase%'
    public static Specification<Consumidor> emailLike (String email) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("email")), "%" + email.toUpperCase() + "%" );
    }

}
