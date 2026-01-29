package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> , JpaSpecificationExecutor<Usuario> {

}
