package com.vulpesfiscal.demo.services.nfce;

import com.vulpesfiscal.demo.controllers.dtos.nfce.EmitDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.EnderEmitDTO;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.entities.Venda;
import org.springframework.stereotype.Service;

@Service
public class EmitenteService {

    public EmitDTO gerarEmitente(Venda venda, Integer estabelecimentoId) {

        Empresa empresa = venda.getEmpresa();

        //Alterar posteriormente para venda.getEstabelecimento()
        Estabelecimento estabelecimento = venda.getEstabelecimento();

        EnderEmitDTO enderEmit = new EnderEmitDTO();
        enderEmit.setXLgr(estabelecimento.getLogradouro());
        enderEmit.setNro(estabelecimento.getNumero());
        enderEmit.setXCpl(estabelecimento.getComplemento());
        enderEmit.setXBairro(estabelecimento.getBairro());
        enderEmit.setCMun(estabelecimento.getMunicipioId());
        enderEmit.setXMun(estabelecimento.getMunicipioId());
        enderEmit.setUF(estabelecimento.getCodUf());
        enderEmit.setCEP(estabelecimento.getCep());
        enderEmit.setCPais("1058");
        enderEmit.setXPais("Brasil");
        enderEmit.setFone(estabelecimento.getTelefone());

        EmitDTO emit = new EmitDTO();
        emit.setCNPJ(estabelecimento.getCnpj());
        emit.setXNome(estabelecimento.getEmpresa().getRazaoSocial());
        emit.setXFant(estabelecimento.getNomeFantasia());
        emit.setEnderEmit(enderEmit);
        emit.setIEST(estabelecimento.getInscricaoEstadual());
        emit.setIM(estabelecimento.getInscricaoMunicipal());
        emit.setCNAE(estabelecimento.getEmpresa().getCnae());
        emit.setCRT(estabelecimento.getEmpresa().getRegimeTributario().getCodigo());

        return emit;
    }

}
