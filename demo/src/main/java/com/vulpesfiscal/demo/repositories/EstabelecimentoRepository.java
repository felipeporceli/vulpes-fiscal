package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EstabelecimentoRepository extends JpaRepository <Estabelecimento, Integer>, JpaSpecificationExecutor<Estabelecimento> {

    boolean existsByCnpj(String cnpj);
    Optional<Estabelecimento> findByCnpj(String cnpj);
}
