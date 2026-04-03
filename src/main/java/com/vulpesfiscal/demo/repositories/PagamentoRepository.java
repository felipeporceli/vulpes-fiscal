package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Pagamento;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Integer>, JpaSpecificationExecutor<Pagamento> {

    Optional<Pagamento> findByIdAndEmpresaIdAndEstabelecimentoId(
            Integer pagamentoId,
            Integer empresaId,
            Integer estabelecimentoId
    );

}
