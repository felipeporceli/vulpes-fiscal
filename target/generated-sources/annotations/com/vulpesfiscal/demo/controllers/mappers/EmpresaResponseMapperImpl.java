package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.EmpresaResponseDTO;
import com.vulpesfiscal.demo.entities.Empresa;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-29T06:41:37-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
)
@Component
public class EmpresaResponseMapperImpl implements EmpresaResponseMapper {

    @Override
    public EmpresaResponseDTO toDto(Empresa empresa) {
        if ( empresa == null ) {
            return null;
        }

        Integer id = null;
        String cnpj = null;
        String ambienteSefaz = null;

        id = empresa.getId();
        cnpj = empresa.getCnpj();
        if ( empresa.getAmbienteSefaz() != null ) {
            ambienteSefaz = empresa.getAmbienteSefaz().name();
        }

        EmpresaResponseDTO empresaResponseDTO = new EmpresaResponseDTO( id, cnpj, ambienteSefaz );

        return empresaResponseDTO;
    }
}
