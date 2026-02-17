package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroEstabelecimentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEstabelecimentoDTO;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-12T19:16:10-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
)
@Component
public class EstabelecimentoMapperImpl implements EstabelecimentoMapper {

    @Override
    public ResultadoPesquisaEstabelecimentoDTO toDTO(Estabelecimento estabelecimento) {
        if ( estabelecimento == null ) {
            return null;
        }

        String cnpj = null;
        String nomeFantasia = null;
        String cidade = null;
        String estado = null;
        StatusEmpresa status = null;
        Boolean matriz = null;

        cnpj = estabelecimento.getCnpj();
        nomeFantasia = estabelecimento.getNomeFantasia();
        cidade = estabelecimento.getCidade();
        estado = estabelecimento.getEstado();
        status = estabelecimento.getStatus();
        matriz = estabelecimento.isMatriz();

        ResultadoPesquisaEstabelecimentoDTO resultadoPesquisaEstabelecimentoDTO = new ResultadoPesquisaEstabelecimentoDTO( cnpj, nomeFantasia, cidade, estado, status, matriz );

        return resultadoPesquisaEstabelecimentoDTO;
    }

    @Override
    public Estabelecimento toEntity(CadastroEstabelecimentoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Estabelecimento estabelecimento = new Estabelecimento();

        estabelecimento.setNomeFantasia( dto.nomeFantasia() );
        estabelecimento.setCnpj( dto.cnpj() );
        estabelecimento.setTelefone( dto.telefone() );
        estabelecimento.setEmail( dto.email() );
        estabelecimento.setInscricaoEstadual( dto.inscricaoEstadual() );
        estabelecimento.setCidade( dto.cidade() );
        estabelecimento.setEstado( dto.estado() );
        estabelecimento.setStatus( dto.status() );
        estabelecimento.setMatriz( dto.matriz() );
        estabelecimento.setInscricaoMunicipal( dto.inscricaoMunicipal() );
        estabelecimento.setDataAbertura( dto.dataAbertura() );

        return estabelecimento;
    }

    @Override
    public Estabelecimento toEntityUpdate(AtualizacaoEstabelecimentoDTO dto, Estabelecimento estabelecimento) {
        if ( dto == null ) {
            return estabelecimento;
        }

        estabelecimento.setNomeFantasia( dto.nomeFantasia() );
        estabelecimento.setTelefone( dto.telefone() );
        estabelecimento.setEmail( dto.email() );
        estabelecimento.setInscricaoEstadual( dto.inscricaoEstadual() );
        estabelecimento.setCidade( dto.cidade() );
        estabelecimento.setEstado( dto.estado() );
        estabelecimento.setStatus( dto.status() );
        estabelecimento.setMatriz( dto.matriz() );
        estabelecimento.setInscricaoMunicipal( dto.inscricaoMunicipal() );
        estabelecimento.setDataAbertura( dto.dataAbertura() );

        return estabelecimento;
    }
}
