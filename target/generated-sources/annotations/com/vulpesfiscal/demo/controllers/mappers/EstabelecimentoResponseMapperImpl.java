package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.EstabelecimentoResponseDTO;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-20T21:17:36-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
)
@Component
public class EstabelecimentoResponseMapperImpl implements EstabelecimentoResponseMapper {

    @Override
    public EstabelecimentoResponseDTO toDto(Estabelecimento estabelecimento) {
        if ( estabelecimento == null ) {
            return null;
        }

        Integer id = null;
        String cnpj = null;
        String cidade = null;
        String email = null;

        id = estabelecimento.getId();
        cnpj = estabelecimento.getCnpj();
        cidade = estabelecimento.getCidade();
        email = estabelecimento.getEmail();

        EstabelecimentoResponseDTO estabelecimentoResponseDTO = new EstabelecimentoResponseDTO( id, cnpj, cidade, email );

        return estabelecimentoResponseDTO;
    }
}
