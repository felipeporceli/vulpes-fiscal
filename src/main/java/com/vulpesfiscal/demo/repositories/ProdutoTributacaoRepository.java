package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.entities.ProdutoTributacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoTributacaoRepository extends JpaRepository<ProdutoTributacao, Integer> {

    Optional<ProdutoTributacao> findByEmpresaIdAndProdutoIdTecnicoAndUf(
            Integer empresaId,
            Integer produtoIdTecnico,
            String uf
    );

    List<ProdutoTributacao> findAllByEmpresaId(Integer empresaId);

    List<ProdutoTributacao> findAllByEmpresaIdAndProdutoIdTecnico(Integer empresaId, Integer produtoIdTecnico);

    List<ProdutoTributacao> findAllByEmpresaIdAndProduto(Integer empresaId, Produto produto);

    @Query(value = "SELECT pt.* FROM produto_tributacao pt INNER JOIN produto p ON pt.produto_id = p.id_tecnico WHERE pt.empresa_id = :empresaId AND p.id_produto = :idProduto AND p.empresa_id = :empresaId", nativeQuery = true)
    List<ProdutoTributacao> findByEmpresaAndIdProduto(@Param("empresaId") Integer empresaId, @Param("idProduto") Integer idProduto);

    boolean existsByEmpresaIdAndProdutoIdTecnicoAndUf(
            Integer empresaId,
            Integer produtoIdTecnico,
            String uf
    );
}
