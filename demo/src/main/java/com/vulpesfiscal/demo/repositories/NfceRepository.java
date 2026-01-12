package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Nfce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NfceRepository extends JpaRepository<Nfce, Integer> {

    @Query("""
        select coalesce(max(n.numero), 0)
        from Nfce n
        where n.empresa.id = :empresaId
          and n.estabelecimento.id = :estabelecimentoId
          and n.serie = :serie
    """)
    Integer buscarUltimoNumero(
            @Param("empresaId") Integer empresaId,
            @Param("estabelecimentoId") Integer estabelecimentoId,
            @Param("serie") Integer serie
    );
}

