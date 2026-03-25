package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.CadastroConsumidorDTO;
import com.vulpesfiscal.demo.controllers.mappers.ConsumidorMapper;
import com.vulpesfiscal.demo.controllers.specs.ConsumidorSpecs;
import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Usuario;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.ConsumidorRepository;
import com.vulpesfiscal.demo.security.SecurityService;
import com.vulpesfiscal.demo.validator.ConsumidorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConsumidorService {

    private final ConsumidorRepository repository;
    private final ConsumidorValidator validator;
    private final EmpresaService empresaService;
    private final EstabelecimentoService estabelecimentoService;
    private final ConsumidorMapper mapper;
    private final SecurityService securityService;



    // Metodo para salvar a nivel de serviço.
    public Consumidor salvar(CadastroConsumidorDTO dto,
                             Integer empresaId,
                             Integer estabelecimentoId) {
        Usuario usuario = securityService.obterUsuariologado();
        Empresa empresa = empresaService.buscarPorId(empresaId);
        Estabelecimento estabelecimento = estabelecimentoService.buscarPorId(estabelecimentoId);
        Consumidor consumidor = mapper.toEntity(dto);
        consumidor.setEmpresa(empresa);
        consumidor.setEstabelecimento(estabelecimento);
        consumidor.setUsuario(usuario);
        validator.validar(consumidor, empresaId, estabelecimentoId);
        return repository.save(consumidor);
    }

    // Metodo para pesquisar com filtro a nível de serviço.
    public Page<Consumidor> pesquisar(Integer empresaId,
                                      Integer id,
                                      String cpf,
                                      String nome,
                                      String email,
                                      Integer pagina,
                                      String uf,
                                      String municipio,
                                      String cep,
                                      String telefone,
                                      Integer tamanhoPagina) {
        // SELECT * FROM consumidor WHERE 0 = 0
        Specification<Consumidor> specification = Specification.where
                ((root, query, cb) -> cb.conjunction());

        specification = specification.and(ConsumidorSpecs.empresaIdIgual(empresaId));

        if (id != null) {
            specification = specification.and(ConsumidorSpecs.idIgual(id));
        }

        if (cpf != null) {
            specification = specification.and(ConsumidorSpecs.cpfLike(cpf));
        }

        if (nome != null) {
            specification = specification.and(ConsumidorSpecs.nomeLike(nome));
        }

        if (email != null) {
            specification = specification.and(ConsumidorSpecs.emailLike(email));
        }

        if (cep != null) {
            specification = specification.and(ConsumidorSpecs.cepIgual(cep));
        }

        if (uf != null) {
            specification = specification.and(ConsumidorSpecs.ufLike(uf));
        }

        if (municipio != null) {
            specification = specification.and(ConsumidorSpecs.municipioLike(municipio));
        }

        if (telefone != null) {
            specification = specification.and(ConsumidorSpecs.telefoneLike(telefone));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(specification, pageRequest);
    }

    @Transactional
    public void deletar(String cpf) {
        validator.validarDeletar(cpf);
        repository.deleteByCpf(cpf);
    }


    @Transactional
    public void atualizar(Consumidor consumidor) {
        Usuario usuario = securityService.obterUsuariologado();
        validator.pesquisarPorCpfEempresa(consumidor.getCpf(), consumidor.getEmpresa().getId());
        consumidor.setAtualizadoPor(usuario);
        repository.save(consumidor);
    }

}