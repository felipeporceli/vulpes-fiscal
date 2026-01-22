package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.specs.EmpresaSpecs;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.validator.EmpresaValidator;
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
    private final EmpresaValidator validator;


    // Metodo para salvar a nivel de serviço.
    public Empresa salvar(Empresa empresa) {
        validator.validar(empresa);
        return repository.save(empresa);
    }

    // Metodo para pesquisar com filtro a nível de serviço.
    public Page<Empresa> pesquisar(String cnpj,
                                   String razaoSocial,
                                   String inscricaoEstadual,
                                   RegimeTributarioEmpresa regimeTributario,
                                   StatusEmpresa statusEmpresa,
                                   PorteEmpresa porte,
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
    public void deletar(String cnpj) {
        validator.validarDeletar(cnpj);
        repository.delete(validator.pesquisarPorCnpj(cnpj));
    }


    public void atualizar(Empresa empresa) {
        validator.pesquisarPorCnpj(empresa.getCnpj());
        repository.save(empresa);
    }

    public Empresa buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                "Empresa não encontrada para o ID informado"
                        )
                );
    }




}