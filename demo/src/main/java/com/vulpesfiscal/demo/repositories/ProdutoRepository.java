package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository <Produto, Integer>, JpaSpecificationExecutor<Produto> {
    boolean existsByCodigoBarras(String codigoBarras);
    List<Produto> findByEmpresaId(Integer empresaId);
    boolean existsByEmpresaIdAndIdProduto(Integer empresaId, Integer idProduto);
    boolean existsByEmpresaIdAndCodigoBarras(Integer empresaId, String codigoBarras);
    Optional<Produto> findByEmpresaIdAndIdProduto(Integer empresaId, Integer idProduto);
    boolean existsByEmpresaIdAndCodigoBarrasAndIdTecnicoNot(
            Integer empresaId,
            String codigoBarras,
            Integer idTecnico
    );

    boolean existsByEmpresaIdAndIdProdutoAndIdTecnicoNot(
            Integer empresaId,
            Integer idProduto,
            Integer idTecnico
    );



}
