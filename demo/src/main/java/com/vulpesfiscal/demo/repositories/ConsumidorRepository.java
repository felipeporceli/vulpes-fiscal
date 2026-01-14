package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsumidorRepository extends JpaRepository<Consumidor, Integer> , JpaSpecificationExecutor<Consumidor> {

        boolean existsByCpf(String cpf);

}
