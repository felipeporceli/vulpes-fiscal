package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoEmpresaDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroEmpresaDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEmpresaDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEstabelecimentoDTO;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-29T06:41:37-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Azul Systems, Inc.)"
)
@Component
public class EmpresaMapperImpl implements EmpresaMapper {

    @Override
    public ResultadoPesquisaEmpresaDTO toDTO(Empresa empresa) {
        if ( empresa == null ) {
            return null;
        }

        String cnpj = null;
        String razaoSocial = null;
        String inscricaoEstadual = null;
        RegimeTributarioEmpresa regimeTributario = null;
        StatusEmpresa status = null;
        PorteEmpresa porte = null;
        List<ResultadoPesquisaEstabelecimentoDTO> estabelecimentos = null;

        cnpj = empresa.getCnpj();
        razaoSocial = empresa.getRazaoSocial();
        inscricaoEstadual = empresa.getInscricaoEstadual();
        regimeTributario = empresa.getRegimeTributario();
        status = empresa.getStatus();
        porte = empresa.getPorte();
        estabelecimentos = estabelecimentoListToResultadoPesquisaEstabelecimentoDTOList( empresa.getEstabelecimentos() );

        ResultadoPesquisaEmpresaDTO resultadoPesquisaEmpresaDTO = new ResultadoPesquisaEmpresaDTO( cnpj, razaoSocial, inscricaoEstadual, regimeTributario, status, porte, estabelecimentos );

        return resultadoPesquisaEmpresaDTO;
    }

    @Override
    public Empresa toEntity(CadastroEmpresaDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Empresa empresa = new Empresa();

        empresa.setId( dto.id() );
        empresa.setRazaoSocial( dto.razaoSocial() );
        empresa.setNomeFantasia( dto.nomeFantasia() );
        empresa.setCnpj( dto.cnpj() );
        empresa.setInscricaoEstadual( dto.inscricaoEstadual() );
        empresa.setRegimeTributario( dto.regimeTributario() );
        empresa.setPorte( dto.porte() );
        empresa.setAmbienteSefaz( dto.ambienteSefaz() );
        empresa.setStatus( dto.status() );
        empresa.setDataAbertura( dto.dataAbertura() );

        return empresa;
    }

    @Override
    public Empresa toEntityUpdate(AtualizacaoEmpresaDTO dto, Empresa empresa) {
        if ( dto == null ) {
            return empresa;
        }

        empresa.setRazaoSocial( dto.razaoSocial() );
        empresa.setNomeFantasia( dto.nomeFantasia() );
        empresa.setInscricaoEstadual( dto.inscricaoEstadual() );
        empresa.setRegimeTributario( dto.regimeTributario() );
        empresa.setPorte( dto.porte() );
        empresa.setAmbienteSefaz( dto.ambienteSefaz() );
        empresa.setStatus( dto.status() );
        empresa.setDataAbertura( dto.dataAbertura() );

        return empresa;
    }

    protected ResultadoPesquisaEstabelecimentoDTO estabelecimentoToResultadoPesquisaEstabelecimentoDTO(Estabelecimento estabelecimento) {
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

    protected List<ResultadoPesquisaEstabelecimentoDTO> estabelecimentoListToResultadoPesquisaEstabelecimentoDTOList(List<Estabelecimento> list) {
        if ( list == null ) {
            return null;
        }

        List<ResultadoPesquisaEstabelecimentoDTO> list1 = new ArrayList<ResultadoPesquisaEstabelecimentoDTO>( list.size() );
        for ( Estabelecimento estabelecimento : list ) {
            list1.add( estabelecimentoToResultadoPesquisaEstabelecimentoDTO( estabelecimento ) );
        }

        return list1;
    }
}
