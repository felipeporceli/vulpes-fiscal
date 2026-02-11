package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoUsuarioDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroUsuarioDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaUsuarioDTO;
import com.vulpesfiscal.demo.entities.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-05T06:44:32-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
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
        usuario.setPerfilId( dto.perfilId() );
        usuario.setNome( dto.nome() );
        usuario.setEmail( dto.email() );
        usuario.setAtivo( dto.ativo() );

        return usuario;
    }

    @Override
    public ResultadoPesquisaUsuarioDTO toDTO(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        Integer perfilId = null;
        String nome = null;
        String email = null;
        Boolean ativo = null;

        perfilId = usuario.getPerfilId();
        nome = usuario.getNome();
        email = usuario.getEmail();
        ativo = usuario.getAtivo();

        Integer empresaId = null;
        Integer estabelecimentoId = null;
        String senhaHash = null;

        ResultadoPesquisaUsuarioDTO resultadoPesquisaUsuarioDTO = new ResultadoPesquisaUsuarioDTO( perfilId, empresaId, estabelecimentoId, nome, email, senhaHash, ativo );

        return resultadoPesquisaUsuarioDTO;
    }

    @Override
    public Usuario toEntityUpdate(AtualizacaoUsuarioDTO dto, Usuario usuario) {
        if ( dto == null ) {
            return usuario;
        }

        usuario.setPerfilId( dto.perfilId() );
        usuario.setNome( dto.nome() );
        usuario.setEmail( dto.email() );
        usuario.setSenha( dto.senha() );
        usuario.setAtivo( dto.ativo() );

        return usuario;
    }
}
