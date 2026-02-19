package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.nfce.NfceDTO;
import com.vulpesfiscal.demo.entities.Nfce;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-17T22:20:06-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
)
@Component
public class NfceMapperImpl implements NfceMapper {

    @Override
    public Nfce toEntity(NfceDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Nfce nfce = new Nfce();

        return nfce;
    }
}
