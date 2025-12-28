package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmpresaRepository extends JpaRepository <Empresa, Integer>, JpaSpecificationExecutor<Empresa> {
}
