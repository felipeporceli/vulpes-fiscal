package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.CadastroEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.mappers.EstabelecimentoMapper;
import com.vulpesfiscal.demo.controllers.specs.EstabelecimentoSpecs;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import com.vulpesfiscal.demo.validator.EstabelecimentoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EstabelecimentoService {

    private final EstabelecimentoRepository repository;
    private final EmpresaService empresaService;
    private final EstabelecimentoValidator validator;
    private final EstabelecimentoMapper mapper;


    /* Salva um estabelecimento vinculado a uma empresa já existente. A empresa é buscada pelo ID informado
    e associada ao estabelecimento antes da persistência, garantindo a integridade do relacionamento. */

    public Estabelecimento salvar(Integer empresaId, CadastroEstabelecimentoDTO dto) {
        Empresa empresa = empresaService.buscarPorId(empresaId);
        Estabelecimento estabelecimento = mapper.toEntity(dto);
        estabelecimento.setEmpresa(empresa);
        validator.validar(estabelecimento);
        return repository.save(estabelecimento);
    }

    // Metodo para pesquisar com filtro a nível de serviço.
    public Page<Estabelecimento> pesquisar(String cnpj,
                                   String nomeFantasia,
                                   String cidade,
                                   String estado,
                                   StatusEmpresa status,
                                   Boolean matriz,
                                   Integer pagina,
                                   Integer tamanhoPagina) {
        // SELECT * FROM Estabelecimento WHERE 0 = 0
        Specification<Estabelecimento> specification = Specification.where
                ((root, query, cb) -> cb.conjunction());

        if (cnpj != null) {
            specification = specification.and(EstabelecimentoSpecs.cnpjIgual(cnpj));

        }
        if (nomeFantasia != null) {
            specification = specification.and(EstabelecimentoSpecs.nomeFantasiaLike(nomeFantasia));
        }

        if (cidade != null) {
            specification = specification.and(EstabelecimentoSpecs.cidadeLike(cidade));
        }

        if (estado != null) {
            specification = specification.and(EstabelecimentoSpecs.estadoIgual(estado));
        }

        if (status != null) {
            specification = specification.and(EstabelecimentoSpecs.statusIgual(status));
        }

        if (matriz != null) {
            specification = specification.and(EstabelecimentoSpecs.matrizIgual(matriz));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(specification, pageRequest);
    }
    public void deletar(String cnpj) {
        validator.pesquisarPorCnpj(cnpj);
        repository.delete(validator.pesquisarPorCnpj(cnpj));
    }


    public void atualizar(Estabelecimento estabelecimento) {
        repository.save(estabelecimento);
    }



}