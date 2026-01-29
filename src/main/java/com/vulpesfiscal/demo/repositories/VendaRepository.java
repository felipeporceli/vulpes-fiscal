package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VendaRepository extends JpaRepository<Venda, Integer> , JpaSpecificationExecutor<Venda> {

}
