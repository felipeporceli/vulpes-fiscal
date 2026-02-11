package com.vulpesfiscal.demo.services.nfce;

import com.vulpesfiscal.demo.controllers.dtos.nfce.DestDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.EnderDestDTO;
import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Venda;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DestinatarioService {

    public DestDTO gerarDestinatario(Venda venda) {

        Consumidor consumidor = venda.getConsumidor();

        // 🔹 NFC-e SEM identificação do consumidor
        if (consumidor == null) {
            return null;
        }

        boolean temCpf = StringUtils.hasText(consumidor.getCpf());
        boolean temCnpj = StringUtils.hasText(consumidor.getCnpj());

        // 🔹 Se não tem CPF nem CNPJ, não envia dest
        if (!temCpf && !temCnpj) {
            return null;
        }

        DestDTO dest = new DestDTO();

        // 🔹 Pessoa Física (cenário mais comum)
        if (temCpf) {
            dest.setCPF(consumidor.getCpf());
            dest.setIndIEDest(9); // Não contribuinte
        }

        // 🔹 Pessoa Jurídica
        if (temCnpj) {
            dest.setCNPJ(consumidor.getCnpj());

            if (StringUtils.hasText(consumidor.getInscricaoEstadual())) {
                dest.setIndIEDest(1); // Contribuinte ICMS
                dest.setIE(consumidor.getInscricaoEstadual());
            } else {
                dest.setIndIEDest(9); // Não contribuinte
            }
        }

        // 🔹 Nome (opcional em NFC-e, mas recomendável)
        if (StringUtils.hasText(consumidor.getNome())) {
            dest.setXNome(consumidor.getNome());
        }

        // 🔹 Email (opcional – DANFE NFC-e)
        if (StringUtils.hasText(consumidor.getEmail())) {
            dest.setEmail(consumidor.getEmail());
        }

        // 🔹 Endereço (somente se existir e estiver minimamente completo)
        if (consumidor.getLogradouro() != null) {
            EnderDestDTO enderDest = montarEndereco(venda.getConsumidor());
            if (enderDest != null) {
                dest.setEnderDest(enderDest);
            }
        }

        return dest;
    }

    /**
     * Monta endereço do destinatário apenas se houver dados suficientes
     */
    private EnderDestDTO montarEndereco(Consumidor consumidor) {

        if (!StringUtils.hasText(consumidor.getLogradouro())
                || !StringUtils.hasText(consumidor.getMunicipioId())
                || !StringUtils.hasText(consumidor.getUf())) {
            return null;
        }

        EnderDestDTO ender = new EnderDestDTO();
        ender.setXLgr(consumidor.getLogradouro());
        ender.setNro(consumidor.getNumero());
        ender.setXCpl(consumidor.getComplemento());
        ender.setXBairro(consumidor.getBairro());
        ender.setCMun(consumidor.getMunicipioId());
        ender.setXMun(consumidor.getMunicipio());
        ender.setUF(consumidor.getUf());
        ender.setCEP(consumidor.getCep());
        ender.setCPais("1058");
        ender.setXPais("Brasil");
        ender.setFone(consumidor.getTelefone());

        return ender;
    }
}
