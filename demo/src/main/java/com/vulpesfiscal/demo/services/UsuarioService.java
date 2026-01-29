package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.CadastroUsuarioDTO;
import com.vulpesfiscal.demo.controllers.mappers.UsuarioMapper;
import com.vulpesfiscal.demo.controllers.specs.PagamentoSpecs;
import com.vulpesfiscal.demo.controllers.specs.UsuarioSpecs;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Usuario;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import com.vulpesfiscal.demo.repositories.UsuarioRepository;
import com.vulpesfiscal.demo.validator.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final EmpresaRepository empresaRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioValidator validator;

    // Metodo para salvar a nivel de serviço.
    public Usuario salvar(Integer empresaId,
                          Integer estabelecimentoId,
                          CadastroUsuarioDTO dto) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada"));

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findByIdAndEmpresaId(estabelecimentoId, empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estabelecimento não pertence à empresa informada"
                ));

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setEmpresa(empresa);
        usuario.setEstabelecimento(estabelecimento);
        return repository.save(usuario);
    }

    public Page<Usuario> pesquisar(Integer id,
                                     Integer perfilId,
                                     String nome,
                                     String email,
                                     Boolean ativo,
                                     Integer empresaId,
                                     Integer estabelecimentoId,
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


        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return repository.findAll(specification, pageRequest);
    }

    public void atualizar(Usuario usuario) {
        validator.validarPesquisar(usuario.getEmpresa().getId(), usuario.getEstabelecimento().getId());
        repository.save(usuario);
    }

    public void deletar(Integer id, Integer empresaId, Integer estabelecimentoId) {
        validator.validarPesquisar(empresaId, estabelecimentoId);
        repository.delete(validator.pesquisarPorId(id));
    }


}
