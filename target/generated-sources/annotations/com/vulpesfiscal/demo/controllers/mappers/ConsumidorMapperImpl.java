package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoConsumidorDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroConsumidorDTO;
import com.vulpesfiscal.demo.controllers.dtos.ConsumidorResponseDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaConsumidorDTO;
import com.vulpesfiscal.demo.entities.Consumidor;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T09:36:53-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
)
@Component
public class ConsumidorMapperImpl implements ConsumidorMapper {

    @Override
    public ResultadoPesquisaConsumidorDTO toDTO(Consumidor consumidor) {
        if ( consumidor == null ) {
            return null;
        }

        Integer id = null;
        String cpf = null;
        String nome = null;
        String email = null;

        id = consumidor.getId();
        cpf = consumidor.getCpf();
        nome = consumidor.getNome();
        email = consumidor.getEmail();

        ResultadoPesquisaConsumidorDTO resultadoPesquisaConsumidorDTO = new ResultadoPesquisaConsumidorDTO( id, cpf, nome, email );

        return resultadoPesquisaConsumidorDTO;
    }

    @Override
    public Consumidor toEntity(CadastroConsumidorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Consumidor consumidor = new Consumidor();

        consumidor.setNome( dto.nome() );
        consumidor.setCpf( dto.cpf() );
        consumidor.setEmail( dto.email() );

        return consumidor;
    }

    @Override
    public Consumidor toEntityUpdate(AtualizacaoConsumidorDTO dto, Consumidor consumidor) {
        if ( dto == null ) {
            return consumidor;
        }

        consumidor.setNome( dto.nome() );
        consumidor.setCpf( dto.cpf() );
        consumidor.setEmail( dto.email() );

        return consumidor;
    }

    @Override
    public ConsumidorResponseDTO toResponseDTO(Consumidor consumidor) {
        if ( consumidor == null ) {
            return null;
        }

        Integer id = null;
        String nome = null;
        String cpf = null;
        String email = null;

        id = consumidor.getId();
        nome = consumidor.getNome();
        cpf = consumidor.getCpf();
        email = consumidor.getEmail();

        ConsumidorResponseDTO consumidorResponseDTO = new ConsumidorResponseDTO( id, nome, cpf, email );

        return consumidorResponseDTO;
    }
}
