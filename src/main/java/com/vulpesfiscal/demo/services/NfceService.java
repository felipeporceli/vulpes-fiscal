package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.entities.Nfce;
import com.vulpesfiscal.demo.entities.Usuario;
import com.vulpesfiscal.demo.entities.Venda;
import com.vulpesfiscal.demo.entities.enums.StatusNfce;
import com.vulpesfiscal.demo.repositories.NfceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NfceService {

    private final NfceRepository nfceRepository;

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
        nfce.setDataCriacao();
        nfce.setCriadoPor(venda.getCriadoPor());
        nfce.setAtualizadoPor(venda.getAtualizadoPor());

        return nfceRepository.save(nfce);
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
}

