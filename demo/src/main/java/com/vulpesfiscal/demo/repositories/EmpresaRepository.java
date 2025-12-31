package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository <Empresa, Integer>, JpaSpecificationExecutor<Empresa> {

    boolean existsByCnpj(String cnpj);
    Optional<Empresa> findByCnpj(String cnpj);
}
