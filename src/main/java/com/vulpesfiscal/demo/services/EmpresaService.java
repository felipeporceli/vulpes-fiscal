package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoEmpresaDTO;
import com.vulpesfiscal.demo.controllers.mappers.EmpresaMapper;
import com.vulpesfiscal.demo.controllers.specs.ConsumidorSpecs;
import com.vulpesfiscal.demo.controllers.specs.EmpresaSpecs;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Usuario;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import com.vulpesfiscal.demo.exceptions.EmpresaOuEstabelecimentoNaoEncontradosException;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.repositories.UsuarioRepository;
import com.vulpesfiscal.demo.security.SecurityService;
import com.vulpesfiscal.demo.validator.EmpresaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository repository;
    private final EmpresaValidator validator;
    private final SecurityService securityService;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaMapper mapper;


    // Metodo para salvar a nivel de serviço.
    public Empresa salvar(Empresa empresa) {
        validator.validar(empresa);

        // Obter usuario logado
        String login = securityService.obterLoginUsuarioLogado();
        Usuario usuarioLogado = usuarioRepository.findByEmail(login);
        empresa.setUsuario(usuarioLogado);

        return repository.save(empresa);
    }

    // Metodo para pesquisar com filtro a nível de serviço.
    public Page<Empresa> pesquisar(String cnpj,
                                   String razaoSocial,
                                   String inscricaoEstadual,
                                   RegimeTributarioEmpresa regimeTributario,
                                   StatusEmpresa statusEmpresa,
                                   PorteEmpresa porte,
                                   Integer empresaId,
                                   Integer pagina,
                                   Integer tamanhoPagina,
                                   String ordenarPor,
                                   String direcao) {

        // Proteção contra requests abusivos: limite máximo de 100 registros por página.
        // Impede que qualquer cliente — mesmo autenticado — faça dump em uma única chamada.
        tamanhoPagina = Math.min(tamanhoPagina, 100);

        // Isolamento multi-tenant: EMPRESARIO e GERENTE só consultam a própria empresa.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdminOuSuporte = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR")
                            || a.getAuthority().equals("ROLE_SUPORTE"));

        if (!isAdminOuSuporte) {
            empresaId = securityService.obterEmpresaId();
        }

        // SELECT * FROM empresa WHERE 0 = 0
        Specification<Empresa> specification = Specification.where
                ((root, query, cb) -> cb.conjunction());


        if (empresaId != null) {
            specification = specification.and(EmpresaSpecs.empresaIdIgual(empresaId));
        }

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

        Sort sort = Sort.unsorted();
        if (ordenarPor != null && !ordenarPor.isBlank()) {
            sort = "desc".equalsIgnoreCase(direcao)
                    ? Sort.by(ordenarPor).descending()
                    : Sort.by(ordenarPor).ascending();
        }
        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina, sort);
        return repository.findAll(specification, pageRequest);
    }
    public void deletar(Integer empresaId) {
        validator.validarDeletar(empresaId);
        repository.deleteById(empresaId);
    }


    public void atualizar(Integer empresaId,
                          AtualizacaoEmpresaDTO dto) {
        Empresa empresa = repository
                .findById(empresaId)
                .orElseThrow(() -> new EmpresaOuEstabelecimentoNaoEncontradosException(
                        "Empresa nao encontrada."
                ));

        mapper.toEntityUpdate(dto, empresa);

        String login = securityService.obterLoginUsuarioLogado();
        Usuario usuarioLogado = usuarioRepository.findByEmail(login);
        empresa.setAtualizadoPor(usuarioLogado);

        repository.save(empresa);
    }

    public Empresa buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                            new EmpresaOuEstabelecimentoNaoEncontradosException(
                                "Empresa não encontrada para o ID informado"
                        )
                );
    }

    public void salvarTokenFocusNfe(Integer empresaId, String token) {
        Empresa empresa = repository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));
        empresa.setTokenFocusNfe(token);
        repository.save(empresa);
    }
}