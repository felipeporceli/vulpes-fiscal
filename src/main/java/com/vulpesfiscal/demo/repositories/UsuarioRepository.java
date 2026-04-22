package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>, JpaSpecificationExecutor<Usuario> {

    Usuario findUsuarioById(Integer id);
    Usuario findByEmail(String email);
    Optional<Usuario> findByIdAndEmpresaId(Integer id, Integer empresaId);
    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM usuario WHERE empresa_id = :empresaId AND 'VENDEDOR' = ANY(roles) AND ativo = true ORDER BY nome", nativeQuery = true)
    List<Usuario> findVendedoresByEmpresaId(@Param("empresaId") Integer empresaId);
}