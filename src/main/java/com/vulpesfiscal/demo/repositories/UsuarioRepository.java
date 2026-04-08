package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>, JpaSpecificationExecutor<Usuario> {

    Usuario findUsuarioById(Integer id);
    Usuario findByEmail(String email);
    Optional<Usuario> findByIdAndEmpresaId(Integer id, Integer empresaId);

    // Verificar se existe por email para validar se o Usuário já tem cadastro no sistema
    boolean existsByEmail(String email);
}