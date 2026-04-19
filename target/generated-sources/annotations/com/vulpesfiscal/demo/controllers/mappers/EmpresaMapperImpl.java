package com.vulpesfiscal.demo.controllers.mappers;

import com.vulpesfiscal.demo.controllers.dtos.AtualizacaoEmpresaDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroEmpresaDTO;
import com.vulpesfiscal.demo.controllers.dtos.ResultadoPesquisaEmpresaDTO;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.enums.AmbienteSefazEmpresa;
import com.vulpesfiscal.demo.entities.enums.PorteEmpresa;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T00:22:50-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class EmpresaMapperImpl implements EmpresaMapper {

    @Override
    public ResultadoPesquisaEmpresaDTO toDTO(Empresa empresa) {
        if ( empresa == null ) {
            return null;
        }

        Integer id = null;
        String razaoSocial = null;
        String nomeFantasia = null;
        String cnpj = null;
        String inscricaoEstadual = null;
        RegimeTributarioEmpresa regimeTributario = null;
        PorteEmpresa porte = null;
        AmbienteSefazEmpresa ambienteSefaz = null;
        StatusEmpresa status = null;
        String cnae = null;
        String uf = null;
        LocalDate dataAbertura = null;

        id = empresa.getId();
        razaoSocial = empresa.getRazaoSocial();
        nomeFantasia = empresa.getNomeFantasia();
        cnpj = empresa.getCnpj();
        inscricaoEstadual = empresa.getInscricaoEstadual();
        regimeTributario = empresa.getRegimeTributario();
        porte = empresa.getPorte();
        ambienteSefaz = empresa.getAmbienteSefaz();
        status = empresa.getStatus();
        cnae = empresa.getCnae();
        uf = empresa.getUf();
        dataAbertura = empresa.getDataAbertura();

        ResultadoPesquisaEmpresaDTO resultadoPesquisaEmpresaDTO = new ResultadoPesquisaEmpresaDTO( id, razaoSocial, nomeFantasia, cnpj, inscricaoEstadual, regimeTributario, porte, ambienteSefaz, status, cnae, uf, dataAbertura );

        return resultadoPesquisaEmpresaDTO;
    }

    @Override
    public Empresa toEntity(CadastroEmpresaDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Empresa empresa = new Empresa();

        empresa.setRazaoSocial( dto.razaoSocial() );
        empresa.setNomeFantasia( dto.nomeFantasia() );
        empresa.setCnpj( dto.cnpj() );
        empresa.setInscricaoEstadual( dto.inscricaoEstadual() );
        empresa.setRegimeTributario( dto.regimeTributario() );
        empresa.setPorte( dto.porte() );
        empresa.setAmbienteSefaz( dto.ambienteSefaz() );
        empresa.setStatus( dto.status() );
        empresa.setCnae( dto.cnae() );
        empresa.setUf( dto.uf() );
        empresa.setDataAbertura( dto.dataAbertura() );

        return empresa;
    }

    @Override
    public Empresa toEntityUpdate(AtualizacaoEmpresaDTO dto, Empresa empresa) {
        if ( dto == null ) {
            return empresa;
        }

        if ( dto.razaoSocial() != null ) {
            empresa.setRazaoSocial( dto.razaoSocial() );
        }
        if ( dto.nomeFantasia() != null ) {
            empresa.setNomeFantasia( dto.nomeFantasia() );
        }
        if ( dto.inscricaoEstadual() != null ) {
            empresa.setInscricaoEstadual( dto.inscricaoEstadual() );
        }
        if ( dto.regimeTributario() != null ) {
            empresa.setRegimeTributario( dto.regimeTributario() );
        }
        if ( dto.porte() != null ) {
            empresa.setPorte( dto.porte() );
        }
        if ( dto.ambienteSefaz() != null ) {
            empresa.setAmbienteSefaz( dto.ambienteSefaz() );
        }
        if ( dto.status() != null ) {
            empresa.setStatus( dto.status() );
        }
        if ( dto.cnae() != null ) {
            empresa.setCnae( dto.cnae() );
        }
        if ( dto.uf() != null ) {
            empresa.setUf( dto.uf() );
        }
        if ( dto.dataAbertura() != null ) {
            empresa.setDataAbertura( dto.dataAbertura() );
        }

        return empresa;
    }
}
