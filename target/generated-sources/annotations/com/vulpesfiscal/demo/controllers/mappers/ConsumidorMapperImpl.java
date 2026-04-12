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
    date = "2026-04-12T14:03:38-0300",
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
        String nome = null;
        String cpf = null;
        String email = null;
        String cnpj = null;
        String estrangeiroId = null;
        String inscricaoEstadual = null;
        String indicadorInscricao = null;
        String inscricaoSuframa = null;
        String inscricaoMunicipal = null;
        String logradouro = null;
        String numero = null;
        String complemento = null;
        String bairro = null;
        String municipioId = null;
        String municipio = null;
        String uf = null;
        String cep = null;
        String paisId = null;
        String pais = null;
        String telefone = null;

        id = consumidor.getId();
        nome = consumidor.getNome();
        cpf = consumidor.getCpf();
        email = consumidor.getEmail();
        cnpj = consumidor.getCnpj();
        estrangeiroId = consumidor.getEstrangeiroId();
        inscricaoEstadual = consumidor.getInscricaoEstadual();
        indicadorInscricao = consumidor.getIndicadorInscricao();
        inscricaoSuframa = consumidor.getInscricaoSuframa();
        inscricaoMunicipal = consumidor.getInscricaoMunicipal();
        logradouro = consumidor.getLogradouro();
        numero = consumidor.getNumero();
        complemento = consumidor.getComplemento();
        bairro = consumidor.getBairro();
        municipioId = consumidor.getMunicipioId();
        municipio = consumidor.getMunicipio();
        uf = consumidor.getUf();
        cep = consumidor.getCep();
        paisId = consumidor.getPaisId();
        pais = consumidor.getPais();
        telefone = consumidor.getTelefone();

        ResultadoPesquisaConsumidorDTO resultadoPesquisaConsumidorDTO = new ResultadoPesquisaConsumidorDTO( id, nome, cpf, email, cnpj, estrangeiroId, inscricaoEstadual, indicadorInscricao, inscricaoSuframa, inscricaoMunicipal, logradouro, numero, complemento, bairro, municipioId, municipio, uf, cep, paisId, pais, telefone );

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
        consumidor.setCnpj( dto.cnpj() );
        consumidor.setEstrangeiroId( dto.estrangeiroId() );
        consumidor.setInscricaoEstadual( dto.inscricaoEstadual() );
        consumidor.setIndicadorInscricao( dto.indicadorInscricao() );
        consumidor.setInscricaoSuframa( dto.inscricaoSuframa() );
        consumidor.setInscricaoMunicipal( dto.inscricaoMunicipal() );
        consumidor.setLogradouro( dto.logradouro() );
        consumidor.setNumero( dto.numero() );
        consumidor.setComplemento( dto.complemento() );
        consumidor.setBairro( dto.bairro() );
        consumidor.setMunicipioId( dto.municipioId() );
        consumidor.setMunicipio( dto.municipio() );
        consumidor.setUf( dto.uf() );
        consumidor.setCep( dto.cep() );
        consumidor.setPaisId( dto.paisId() );
        consumidor.setPais( dto.pais() );
        consumidor.setTelefone( dto.telefone() );

        return consumidor;
    }

    @Override
    public Consumidor toEntityUpdate(AtualizacaoConsumidorDTO dto, Consumidor consumidor) {
        if ( dto == null ) {
            return consumidor;
        }

        if ( dto.nome() != null ) {
            consumidor.setNome( dto.nome() );
        }
        if ( dto.email() != null ) {
            consumidor.setEmail( dto.email() );
        }
        if ( dto.cnpj() != null ) {
            consumidor.setCnpj( dto.cnpj() );
        }
        if ( dto.estrangeiroId() != null ) {
            consumidor.setEstrangeiroId( dto.estrangeiroId() );
        }
        if ( dto.inscricaoEstadual() != null ) {
            consumidor.setInscricaoEstadual( dto.inscricaoEstadual() );
        }
        if ( dto.indicadorInscricao() != null ) {
            consumidor.setIndicadorInscricao( dto.indicadorInscricao() );
        }
        if ( dto.inscricaoSuframa() != null ) {
            consumidor.setInscricaoSuframa( dto.inscricaoSuframa() );
        }
        if ( dto.inscricaoMunicipal() != null ) {
            consumidor.setInscricaoMunicipal( dto.inscricaoMunicipal() );
        }
        if ( dto.logradouro() != null ) {
            consumidor.setLogradouro( dto.logradouro() );
        }
        if ( dto.numero() != null ) {
            consumidor.setNumero( dto.numero() );
        }
        if ( dto.complemento() != null ) {
            consumidor.setComplemento( dto.complemento() );
        }
        if ( dto.bairro() != null ) {
            consumidor.setBairro( dto.bairro() );
        }
        if ( dto.municipioId() != null ) {
            consumidor.setMunicipioId( dto.municipioId() );
        }
        if ( dto.municipio() != null ) {
            consumidor.setMunicipio( dto.municipio() );
        }
        if ( dto.uf() != null ) {
            consumidor.setUf( dto.uf() );
        }
        if ( dto.cep() != null ) {
            consumidor.setCep( dto.cep() );
        }
        if ( dto.paisId() != null ) {
            consumidor.setPaisId( dto.paisId() );
        }
        if ( dto.pais() != null ) {
            consumidor.setPais( dto.pais() );
        }
        if ( dto.telefone() != null ) {
            consumidor.setTelefone( dto.telefone() );
        }

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
