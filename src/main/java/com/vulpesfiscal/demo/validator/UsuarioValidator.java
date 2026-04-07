package com.vulpesfiscal.demo.validator;


import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Usuario;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.exceptions.UsuarioCadastradoException;
import com.vulpesfiscal.demo.exceptions.UsuarioNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import com.vulpesfiscal.demo.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioValidator {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;


    public void validarPesquisar(Integer empresaId,
                                 Integer estabelecimentoId) {

        if (empresaId != null && estabelecimentoId != null) {
            Estabelecimento estabelecimento = estabelecimentoRepository
                    .findByIdAndEmpresaId(estabelecimentoId, empresaId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Estabelecimento ou Empresa nao encontrados."
                    ));
        }


    }


    public Usuario pesquisarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException("Usuario nao encontrado para o id informado")
                );
    }

    public void validarSalvar(Usuario usuario) {
        boolean existe = usuarioRepository
                .existsByEmail(usuario.getEmail());

        if (existe) {
            throw new UsuarioCadastradoException("Já existe um usuário com esse e-mail cadastrado.");
        }
    }
}
