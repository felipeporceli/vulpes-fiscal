package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "senha", target = "senha")
    Usuario toEntity(CadastroUsuarioDTO dto);

    ResultadoPesquisaUsuarioDTO toDTO(Usuario usuario);

    public abstract Usuario toEntityUpdate(AtualizacaoUsuarioDTO dto, @MappingTarget Usuario usuario);


}
