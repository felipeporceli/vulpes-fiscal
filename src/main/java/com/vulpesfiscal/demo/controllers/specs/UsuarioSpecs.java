package com.vulpesfiscal.demo.controllers.specs;

import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Usuario;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class UsuarioSpecs {

    public static Specification<Usuario> idIgual (Integer idUsuario) {
        return (root, query, cb) -> cb.equal(root.get("id"), idUsuario);
    }

    public static Specification<Usuario> empresaIdIgual(Integer empresaId) {
        return (root, query, cb) ->
                cb.equal(root.get("empresa").get("id"), empresaId);
    }

    public static Specification<Usuario> estabelecimentoIdIgual(Integer estabelecimentoId) {
        return (root, query, cb) ->
                cb.equal(root.get("estabelecimento").get("id"), estabelecimentoId);
    }

    public static Specification<Usuario> nomeLike (String nome) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("nome")), "%" + nome.toUpperCase() + "%" );
    }

    public static Specification<Usuario> emailLike (String email) {
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("nome")), "%" + email.toUpperCase() + "%" );
    }


    public static Specification<Usuario> ativoIgual (Boolean ativo) {
        return (root, query, cb) -> cb.equal(root.get("ativo"), ativo);
    }

    public static Specification<Usuario> perfilIdIgual (Integer perfilId) {
        return (root, query, cb) -> cb.equal(root.get("perfilId"), perfilId);
    }
}
