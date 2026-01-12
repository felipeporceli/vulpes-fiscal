package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.ItemNfce;
import com.vulpesfiscal.demo.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ItemNfceRepository extends JpaRepository <ItemNfce, Integer>, JpaSpecificationExecutor<ItemNfce>
{

}
