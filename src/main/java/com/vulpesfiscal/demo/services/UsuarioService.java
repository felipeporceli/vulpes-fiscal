package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoUsuarioDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroUsuarioDTO;
import com.vulpesfiscal.demo.controllers.mappers.UsuarioMapper;
import com.vulpesfiscal.demo.controllers.specs.PagamentoSpecs;
import com.vulpesfiscal.demo.controllers.specs.UsuarioSpecs;
import com.vulpesfiscal.demo.entities.*;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.exceptions.UsuarioNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import com.vulpesfiscal.demo.repositories.UsuarioRepository;
import com.vulpesfiscal.demo.security.SecurityService;
import com.vulpesfiscal.demo.validator.UsuarioValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final EmpresaRepository empresaRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioValidator validator;
    private final PasswordEncoder encoder;
    private final SecurityService securityService;
    private final UsuarioMapper mapper;

    // Metodo para salvar a nivel de serviço.
    public Usuario salvar(Integer empresaId, Integer estabelecimentoId, Usuario usuario) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada"));

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findByIdAndEmpresaId(estabelecimentoId, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estabelecimento não pertence à empresa informada"
                ));

        // Obter usuario logado
        String login = securityService.obterLoginUsuarioLogado();
        Usuario usuarioLogado = repository.findByEmail(login);

        usuario.setEmpresa(empresa);
        usuario.setEstabelecimento(estabelecimento);
        usuario.setUsuario(usuarioLogado);

        validator.validarSalvar(usuario);

        usuario.setSenha(encoder.encode(usuario.getSenha()));

        return repository.save(usuario);
    }

    public Page<Usuario> pesquisar(Integer id,
                                     Integer perfilId,
                                     String nome,
                                     String email,
                                     Boolean ativo,
                                     Integer empresaId,
                                     Integer estabelecimentoId,
                                     String username,
                                     String cpf,
                                     String roles,
                                     String telefone,
                                     Integer pagina,
                                     Integer tamanhoPagina) {

        validator.validarPesquisar(empresaId, estabelecimentoId);

        // SELECT * FROM Estabelecimento WHERE 0 = 0
        Specification<Usuario> specification = Specification.where
                ((root, query, cb) -> cb.conjunction());

        if (id != null) {
            specification = specification.and(UsuarioSpecs.idIgual(id));

        }
        if (perfilId != null) {
            specification = specification.and(UsuarioSpecs.perfilIdIgual(perfilId));
        }

        if (nome != null) {
            specification = specification.and(UsuarioSpecs.nomeLike(nome));
        }

        if (empresaId != null) {
            specification = specification.and(UsuarioSpecs.empresaIdIgual(empresaId));
        }

        if (email != null) {
            specification = specification.and(UsuarioSpecs.emailLike(email));
        }

        if (ativo != null) {
            specification = specification.and(UsuarioSpecs.ativoIgual(ativo));
        }

        if (estabelecimentoId != null) {
            specification = specification.and(UsuarioSpecs.estabelecimentoIdIgual(estabelecimentoId));
        }

        if (username != null) {
            specification = specification.and(UsuarioSpecs.usernameLike(username));
        }

        if (cpf != null) {
            specification = specification.and(UsuarioSpecs.cpfLike(cpf));
        }

        if (roles != null) {
            specification = specification.and(UsuarioSpecs.roleLike(roles));
        }

        if (telefone != null) {
            specification = specification.and(UsuarioSpecs.telefoneLike(telefone));
        }


        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(specification, pageRequest);
    }

    @Transactional
    public void atualizar(Integer id,
                          Integer empresaId,
                          Integer estabelecimentoId,
                          AtualizacaoUsuarioDTO dto) {

        Usuario usuario = repository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                        "Usuário não encontrado."
                ));

        mapper.toEntityUpdate(dto, usuario);

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(encoder.encode(dto.senha()));
        }

        String login = securityService.obterLoginUsuarioLogado();
        Usuario usuarioLogado = repository.findByEmail(login);
        usuario.setAtualizadoPor(usuarioLogado);
        repository.save(usuario);
    }

    public void deletar(Integer id, Integer empresaId, Integer estabelecimentoId) {
        validator.validarPesquisar(empresaId, estabelecimentoId);
        repository.delete(validator.pesquisarPorId(id));
    }

    public Usuario obterPorEmail (String email) {
        return repository.findByEmail(email);
    }

}