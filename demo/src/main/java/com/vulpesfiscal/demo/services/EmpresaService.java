package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.specs.EmpresaSpecs;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository repository;


    public Empresa salvar(Empresa empresa) {
        return repository.save(empresa);
    }

    public Page<Empresa> pesquisa(String cnpj,
                                  String razaoSocial,
                                  String inscricaoEstadual,
                                  String regimeTributario,
                                  StatusEmpresa statusEmpresa,
                                  Integer pagina,
                                  Integer tamanhoPagina) {
        // SELECT * FROM empresa WHERE 0 = 0
        Specification<Empresa> specification = Specification.where
                ((root, query, cb) -> cb.conjunction());

        if (cnpj != null) {
            specification = specification.and(EmpresaSpecs.cnpjIgual(cnpj));

        }
        if (razaoSocial != null) {
            specification = specification.and(EmpresaSpecs.razaoSocialLike(razaoSocial));
        }

        if (inscricaoEstadual != null) {
            specification = specification.and(EmpresaSpecs.inscricaoIgual(inscricaoEstadual));
        }

        if (regimeTributario != null) {
            specification = specification.and(EmpresaSpecs.regimeTributarioIgual(regimeTributario));
        }

        if (statusEmpresa != null) {
            specification = specification.and(EmpresaSpecs.statusIgual(statusEmpresa));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(specification, pageRequest);

    }
}
