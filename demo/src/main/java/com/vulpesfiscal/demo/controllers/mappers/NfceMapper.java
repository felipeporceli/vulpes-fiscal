package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.*;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Nfce;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.repositories.ProdutoRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(
        componentModel = "spring",
        uses = ItemNfceMapper.class
)
public interface NfceMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", source = "empresa")
    @Mapping(target = "estabelecimento", source = "estabelecimento")
    @Mapping(target = "usuario", source = "usuarioId")
    @Mapping(target = "numero", source = "numero")
    @Mapping(target = "serie", source = "serie")
    @Mapping(target = "dataEmissao", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", constant = "PENDENTE")
    @Mapping(target = "chaveAcesso", ignore = true)
    @Mapping(target = "protocoloAutorizacao", ignore = true)
    @Mapping(target = "valorTotal", ignore = true)
    @Mapping(target = "itens", ignore = true)

    // Campos de auditoria, ignorar no Mapper.
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "atualizadoPor", ignore = true)
    Nfce toEntity(
            CadastroNfceDTO dto,
            Empresa empresa,
            Estabelecimento estabelecimento,
            Integer usuarioId,
            Integer numero,
            Integer serie
    );
}


