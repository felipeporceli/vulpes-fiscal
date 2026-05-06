package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Usuario;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "senha", target = "senha")
    Usuario toEntity(CadastroUsuarioDTO dto);

    @Mapping(source = "empresa.id",        target = "empresaId")
    @Mapping(source = "estabelecimento.id", target = "estabelecimentoId")
    @Mapping(target = "senhaHash",          ignore = true)
    ResultadoPesquisaUsuarioDTO toDTO(Usuario usuario);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Usuario toEntityUpdate(AtualizacaoUsuarioDTO dto, @MappingTarget Usuario usuario);


}
