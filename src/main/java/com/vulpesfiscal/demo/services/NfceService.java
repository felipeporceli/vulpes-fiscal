package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.nfce.*;
import com.vulpesfiscal.demo.entities.*;
import com.vulpesfiscal.demo.entities.enums.StatusNfce;
import com.vulpesfiscal.demo.repositories.NfceRepository;
import com.vulpesfiscal.demo.services.nfce.DestinatarioService;
import com.vulpesfiscal.demo.services.nfce.ProdService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class NfceService {

    private final NfceRepository nfceRepository;
    private final EmpresaService empresaService;
    private final DestinatarioService destinatarioService;
    private final ProdService prodService;
    private final EstabelecimentoService estabelecimentoService;

    /**
     * Emite NFC-e a partir de uma Venda
     * Só cria NFC-e se venda.emitirNfce == true
     */
    @Transactional
    public Nfce emitirNfceSeNecessario(Venda venda) {

        if (Boolean.FALSE.equals(venda.getEmitirNfce())) {
            return null; // simplesmente não emite
        }

        validarVenda(venda);

        Integer proximoNumero = gerarProximoNumero(
                venda.getEmpresa().getId(),
                venda.getEstabelecimento().getId()
        );

        Nfce nfce = new Nfce();
        nfce.setEmpresa(venda.getEmpresa());
        nfce.setEstabelecimento(venda.getEstabelecimento());

        // Usuário fixo por enquanto
        Usuario usuario = new Usuario();
        usuario.setId(2);
        nfce.setUsuario(usuario);

        nfce.setNumero(proximoNumero);
        nfce.setSerie(1);
        nfce.setValorTotal(venda.getValorTotal().intValue());
        nfce.setStatusNfce(StatusNfce.GERADA);
        nfce.setCriadoPor(venda.getCriadoPor());
        nfce.setAtualizadoPor(venda.getAtualizadoPor());

        return nfceRepository.save(nfce);

        /*DestDTO dest = destinatarioService.gerarDestinatario(venda);
        nfce.setDest(dest); // se for null, Jackson ignora*/
    }

    // ======================
    // MÉTODOS AUXILIARES
    // ======================

    private Integer gerarProximoNumero(Integer empresaId, Integer estabelecimentoId) {
        Integer ultimoNumero =
                nfceRepository.buscarUltimoNumero(empresaId, estabelecimentoId);

        return (ultimoNumero == null) ? 1 : ultimoNumero + 1;
    }

    private void validarVenda(Venda venda) {

        if (venda.getEmpresa() == null) {
            throw new IllegalArgumentException("Venda sem empresa vinculada");
        }

        if (venda.getEstabelecimento() == null) {
            throw new IllegalArgumentException("Venda sem estabelecimento vinculado");
        }

        if (!venda.getEstabelecimento().getEmpresa().getId()
                .equals(venda.getEmpresa().getId())) {
            throw new IllegalArgumentException(
                    "Estabelecimento não pertence à empresa informada"
            );
        }

        if (venda.getValorTotal() == null || venda.getValorTotal().signum() <= 0) {
            throw new IllegalArgumentException("Valor total da venda inválido");
        }
    }

    public IdeDTO gerarIdentificacaoNfce (Venda venda) {
            Estabelecimento estabelecimento = venda.getEstabelecimento();

            IdeDTO dto = IdeDTO.builder()
                    .cUF(estabelecimento.getCodUf())
                    .natOp("Venda de Mercadoria")
                    .mod("65")
                    .serie("1")
                    .nNF(String.valueOf(venda.getNfce().getNumero()))
                    .dhEmi(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    .dhSaiEnt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    .tpNF("1")
                    .idDest("1")
                    .cMunFG(estabelecimento.getMunicipioId())
                    .tpImp("4")
                    .tpEmis("1")
                    .tpAmb("2")
                    .finNFe("1")
                    .indFinal("1")
                    .indPres("1")
                    .procEmi("0")
                    .verProc("Vulpes Fiscal 0.0.1 beta")
                    .build();

            return dto;
        }

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
        emit.setIE(estabelecimento.getInscricaoEstadual());
        emit.setIM(estabelecimento.getInscricaoMunicipal());
        emit.setCNAE(estabelecimento.getEmpresa().getCnae());
        emit.setCRT(estabelecimento.getEmpresa().getRegimeTributario().getCodigo());

        return emit;
    }

    public NfceDTO gerarNfce(Venda venda, Integer estabelecimentoId) {

        NfceDTO nfce = new NfceDTO();

        nfce.setIde(gerarIdentificacaoNfce(venda));
        nfce.setEmit(gerarEmitente(venda, estabelecimentoId));
        nfce.setDest(destinatarioService.gerarDestinatario(venda));

        // retirada, entrega, avulsa = null
        nfce.setDet(prodService.montarItens(venda));

        return nfce;
    }





}

