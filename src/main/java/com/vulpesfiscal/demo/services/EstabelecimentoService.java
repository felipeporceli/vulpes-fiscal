package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoUsuarioDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.mappers.EstabelecimentoMapper;
import com.vulpesfiscal.demo.controllers.specs.ConsumidorSpecs;
import com.vulpesfiscal.demo.controllers.specs.EstabelecimentoSpecs;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Usuario;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.exceptions.EmpresaOuEstabelecimentoNaoEncontradosException;
import com.vulpesfiscal.demo.exceptions.EstabelecimentoNaoEncontrado;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.exceptions.UsuarioNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import com.vulpesfiscal.demo.repositories.UsuarioRepository;
import com.vulpesfiscal.demo.security.SecurityService;
import com.vulpesfiscal.demo.validator.EstabelecimentoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EstabelecimentoService {

    private final EstabelecimentoRepository repository;
    private final EmpresaService empresaService;
    private final EstabelecimentoValidator validator;
    private final EstabelecimentoMapper mapper;
    private final SecurityService securityService;
    private final UsuarioRepository usuarioRepository;

    /* Salva um estabelecimento vinculado a uma empresa já existente. A empresa é buscada pelo ID informado
    e associada ao estabelecimento antes da persistência, garantindo a integridade do relacionamento. */

    public Estabelecimento salvar(Integer empresaId, CadastroEstabelecimentoDTO dto) {
        Empresa empresa = empresaService.buscarPorId(empresaId);
        Estabelecimento estabelecimento = mapper.toEntity(dto);

        // Obter usuario logado
        String login = securityService.obterLoginUsuarioLogado();
        Usuario usuarioLogado = usuarioRepository.findByEmail(login);
        estabelecimento.setUsuario(usuarioLogado);

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
                                   Integer tamanhoPagina,
                                           Integer empresaId) {
        // SELECT * FROM Estabelecimento WHERE 0 = 0
        Specification<Estabelecimento> specification = Specification.where
                ((root, query, cb) -> cb.conjunction());

        specification = specification.and(EstabelecimentoSpecs.empresaIdIgual(empresaId));

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

    public void deletar(Integer id) {
        Estabelecimento estabelecimento = repository.findById(id)
                .orElseThrow(() ->
                new EmpresaOuEstabelecimentoNaoEncontradosException("Estabelecimento ou Empresa não encontrados")
                );
        repository.delete(estabelecimento);
    }


    public void atualizar(
                          Integer empresaId,
                          Integer estabelecimentoId,
                          AtualizacaoEstabelecimentoDTO dto) {
        Estabelecimento estabelecimento = repository.findByIdAndEmpresaId(estabelecimentoId, empresaId)
                .orElseThrow(() -> new EstabelecimentoNaoEncontrado(
                        "Estabelecimento nao encontrado."
                ));

        mapper.toEntityUpdate(dto, estabelecimento);

        // Obter usuario logado
        String login = securityService.obterLoginUsuarioLogado();
        Usuario usuarioLogado = usuarioRepository.findByEmail(login);
        estabelecimento.setAtualizadoPor(usuarioLogado);
        repository.save(estabelecimento);
    }

    public Estabelecimento buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new EstabelecimentoNaoEncontrado("Estabelecimento não encontrado para o id informado")
                );
    }



}