package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Nfce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NfceRepository extends JpaRepository<Nfce, Integer> {

    @Query("""
        SELECT MAX(n.numero)
        FROM Nfce n
        WHERE n.empresa.id = :empresaId
          AND n.estabelecimento.id = :estabelecimentoId
    """)
    Integer buscarUltimoNumero(
            @Param("empresaId") Integer empresaId,
            @Param("estabelecimentoId") Integer estabelecimentoId
    );
}
