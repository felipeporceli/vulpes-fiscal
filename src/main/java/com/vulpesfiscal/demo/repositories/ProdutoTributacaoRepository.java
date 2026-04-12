package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.ProdutoTributacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoTributacaoRepository extends JpaRepository<ProdutoTributacao, Integer> {

    Optional<ProdutoTributacao> findByEmpresaIdAndProdutoIdTecnicoAndUf(
            Integer empresaId,
            Integer produtoIdTecnico,
            String uf
    );

    List<ProdutoTributacao> findAllByEmpresaId(Integer empresaId);

    List<ProdutoTributacao> findAllByEmpresaIdAndProdutoIdTecnico(Integer empresaId, Integer produtoIdTecnico);

    boolean existsByEmpresaIdAndProdutoIdTecnicoAndUf(
            Integer empresaId,
            Integer produtoIdTecnico,
            String uf
    );
}
