package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemVendaRepository extends JpaRepository<ItemVenda, Integer> , JpaSpecificationExecutor<ItemVenda> {

}
