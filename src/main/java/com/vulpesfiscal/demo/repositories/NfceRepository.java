package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Nfce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NfceRepository extends JpaRepository<Nfce, Integer> {

    @Query("""
   SELECT COALESCE(MAX(CAST(n.numero AS integer)), 0)
   FROM Nfce n
   WHERE n.estabelecimento.id = :estabelecimentoId
     AND n.serie = :serie
    """)
    Integer buscarUltimoNumero(@Param("estabelecimentoId") Integer estabelecimentoId,
                               @Param("serie") Integer serie);

}
