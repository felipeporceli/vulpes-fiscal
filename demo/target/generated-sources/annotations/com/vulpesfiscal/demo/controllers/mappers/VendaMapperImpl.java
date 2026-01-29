package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.ConsumidorResponseDTO;
import com.vulpesfiscal.demo.controllers.dtos.VendaResponseDTO;
import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Venda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-27T06:51:56-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
)
@Component
public class VendaMapperImpl implements VendaMapper {

    @Override
    public VendaResponseDTO toResponseDTO(Venda venda) {
        if ( venda == null ) {
            return null;
        }

        Integer empresaId = null;
        Integer id = null;
        LocalDateTime dataCriacao = null;
        Integer criadoPor = null;
        LocalDateTime atualizadoEm = null;
        Integer atualizadoPor = null;
        BigDecimal desconto = null;
        ConsumidorResponseDTO consumidor = null;

        empresaId = vendaEmpresaId( venda );
        id = venda.getId();
        dataCriacao = venda.getDataCriacao();
        criadoPor = venda.getCriadoPor();
        atualizadoEm = venda.getAtualizadoEm();
        atualizadoPor = venda.getAtualizadoPor();
        desconto = venda.getDesconto();
        consumidor = consumidorToConsumidorResponseDTO( venda.getConsumidor() );

        VendaResponseDTO vendaResponseDTO = new VendaResponseDTO( id, dataCriacao, criadoPor, atualizadoEm, atualizadoPor, desconto, empresaId, consumidor );

        return vendaResponseDTO;
    }

    private Integer vendaEmpresaId(Venda venda) {
        Empresa empresa = venda.getEmpresa();
        if ( empresa == null ) {
            return null;
        }
        return empresa.getId();
    }

    protected ConsumidorResponseDTO consumidorToConsumidorResponseDTO(Consumidor consumidor) {
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
