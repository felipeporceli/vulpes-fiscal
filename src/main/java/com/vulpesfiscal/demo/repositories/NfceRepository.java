package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Nfce;
import com.vulpesfiscal.demo.entities.enums.StatusNfce;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NfceRepository extends JpaRepository<Nfce, Integer> {

    @Query("""
   SELECT COALESCE(MAX(CAST(n.numero AS integer)), 0)
   FROM Nfce n
   WHERE n.estabelecimento.id = :estabelecimentoId
     AND n.serie = :serie
    """)
    Integer buscarUltimoNumero(@Param("estabelecimentoId") Integer estabelecimentoId,
                               @Param("serie") Integer serie);

    @Query("""
        SELECT n FROM Nfce n
        WHERE n.empresa.id = :empresaId
          AND (:estabelecimentoId IS NULL OR n.estabelecimento.id = :estabelecimentoId)
          AND (:statusNfce IS NULL OR n.statusNfce = :statusNfce)
          AND (:chaveAcesso IS NULL OR n.chaveAcesso LIKE %:chaveAcesso%)
          AND (:numero IS NULL OR n.numero = :numero)
          AND (:dataInicio IS NULL OR n.dataCriacao >= :dataInicio)
          AND (:dataFim IS NULL OR n.dataCriacao <= :dataFim)
        ORDER BY n.dataCriacao DESC
    """)
    Page<Nfce> pesquisar(
            @Param("empresaId")        Integer empresaId,
            @Param("estabelecimentoId") Integer estabelecimentoId,
            @Param("statusNfce")       StatusNfce statusNfce,
            @Param("chaveAcesso")      String chaveAcesso,
            @Param("numero")           String numero,
            @Param("dataInicio")       LocalDateTime dataInicio,
            @Param("dataFim")          LocalDateTime dataFim,
            Pageable pageable
    );

    Optional<Nfce> findByIdAndEmpresaId(Integer id, Integer empresaId);
}
