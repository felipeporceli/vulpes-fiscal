package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoUsuarioDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroUsuarioDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaUsuarioDTO;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-06T20:31:37-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public Usuario toEntity(CadastroUsuarioDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setSenha( dto.senha() );
        usuario.setNome( dto.nome() );
        usuario.setEmail( dto.email() );
        usuario.setUsername( dto.username() );
        usuario.setAtivo( dto.ativo() );
        List<String> list = dto.roles();
        if ( list != null ) {
            usuario.setRoles( new ArrayList<String>( list ) );
        }
        usuario.setCpf( dto.cpf() );
        usuario.setTelefone( dto.telefone() );

        return usuario;
    }

    @Override
    public ResultadoPesquisaUsuarioDTO toDTO(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        Integer empresaId = null;
        Integer estabelecimentoId = null;
        Integer id = null;
        String nome = null;
        String email = null;
        String username = null;
        String telefone = null;
        Boolean ativo = null;
        List<String> roles = null;

        empresaId = usuarioEmpresaId( usuario );
        estabelecimentoId = usuarioEstabelecimentoId( usuario );
        id = usuario.getId();
        nome = usuario.getNome();
        email = usuario.getEmail();
        username = usuario.getUsername();
        telefone = usuario.getTelefone();
        ativo = usuario.getAtivo();
        List<String> list = usuario.getRoles();
        if ( list != null ) {
            roles = new ArrayList<String>( list );
        }

        String senhaHash = null;

        ResultadoPesquisaUsuarioDTO resultadoPesquisaUsuarioDTO = new ResultadoPesquisaUsuarioDTO( id, empresaId, estabelecimentoId, nome, email, username, telefone, senhaHash, ativo, roles );

        return resultadoPesquisaUsuarioDTO;
    }

    @Override
    public Usuario toEntityUpdate(AtualizacaoUsuarioDTO dto, Usuario usuario) {
        if ( dto == null ) {
            return usuario;
        }

        if ( dto.nome() != null ) {
            usuario.setNome( dto.nome() );
        }
        if ( dto.email() != null ) {
            usuario.setEmail( dto.email() );
        }
        if ( dto.senha() != null ) {
            usuario.setSenha( dto.senha() );
        }
        if ( dto.username() != null ) {
            usuario.setUsername( dto.username() );
        }
        if ( dto.ativo() != null ) {
            usuario.setAtivo( dto.ativo() );
        }
        if ( dto.telefone() != null ) {
            usuario.setTelefone( dto.telefone() );
        }

        return usuario;
    }

    private Integer usuarioEmpresaId(Usuario usuario) {
        Empresa empresa = usuario.getEmpresa();
        if ( empresa == null ) {
            return null;
        }
        return empresa.getId();
    }

    private Integer usuarioEstabelecimentoId(Usuario usuario) {
        Estabelecimento estabelecimento = usuario.getEstabelecimento();
        if ( estabelecimento == null ) {
            return null;
        }
        return estabelecimento.getId();
    }
}
