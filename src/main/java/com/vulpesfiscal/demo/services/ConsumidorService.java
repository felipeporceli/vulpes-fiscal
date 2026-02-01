package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.CadastroConsumidorDTO;
import com.vulpesfiscal.demo.controllers.mappers.ConsumidorMapper;
import com.vulpesfiscal.demo.controllers.specs.ConsumidorSpecs;
import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.ConsumidorRepository;
import com.vulpesfiscal.demo.validator.ConsumidorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumidorService {

    private final ConsumidorRepository repository;
    private final ConsumidorValidator validator;
    private final EmpresaService empresaService;
    private final EstabelecimentoService estabelecimentoService;
    private final ConsumidorMapper mapper;


    // Metodo para salvar a nivel de serviço.
    public Consumidor salvar(CadastroConsumidorDTO dto,
                             Integer empresaId,
                             Integer estabelecimentoId) {
        Empresa empresa = empresaService.buscarPorId(empresaId);
        Estabelecimento estabelecimento = estabelecimentoService.buscarPorId(estabelecimentoId);
        Consumidor consumidor = mapper.toEntity(dto);
        consumidor.setEmpresa(empresa);
        consumidor.setEstabelecimento(estabelecimento);
        validator.validar(consumidor);
        return repository.save(consumidor);
    }

    // Metodo para pesquisar com filtro a nível de serviço.
    public Page<Consumidor> pesquisar(Integer id,
                                      String cpf,
                                      String nome,
                                      String email,
                                      Integer pagina,
                                      Integer tamanhoPagina) {
        // SELECT * FROM Consumidor WHERE 0 = 0
        Specification<Consumidor> specification = Specification.where
                ((root, query, cb) -> cb.conjunction());

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

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(specification, pageRequest);
    }
    public void deletar(Integer id) {
        validator.validarDeletar(id);
        repository.deleteById(id);
    }


    public void atualizar(Consumidor Consumidor) {
        validator.pesquisarPorId(Consumidor.getId());
        repository.save(Consumidor);
    }

}