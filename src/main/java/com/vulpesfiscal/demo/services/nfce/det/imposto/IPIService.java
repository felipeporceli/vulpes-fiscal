package com.vulpesfiscal.demo.services.nfce.det.imposto;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ipi.IPIDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ipi.IPITribDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.ipi.IPIntDTO;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.ItemVenda;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class IPIService {

    private static final BigDecimal CEM = new BigDecimal("100");

    public IPIDTO gerarIPI(ItemVenda item) {

        // Adicionar método para verificar se a empresa e industria ou nao
        String teste = "Comércio";
        if (teste != "industria") {
            return gerarIPINT();
        }

        return gerarIPITrib(item);
    }

    private IPIDTO gerarIPITrib(ItemVenda item) {

        BigDecimal base = item.getValorTotal();
        BigDecimal aliquota = new BigDecimal("5.00");

        BigDecimal valorIPI = base.multiply(aliquota)
                .divide(CEM, 2, RoundingMode.HALF_UP);

        IPITribDTO trib = new IPITribDTO();
        trib.setCST("50");
        trib.setVBC(base);
        trib.setPIPI(aliquota);
        trib.setVIPI(valorIPI);

        IPIDTO ipi = new IPIDTO();
        ipi.setCEnq("999");
        ipi.setIpiTribDTO(trib);

        return ipi;
    }

    private IPIDTO gerarIPINT() {

        IPIntDTO nt = new IPIntDTO();
        nt.setCst("53");

        IPIDTO ipi = new IPIDTO();
        ipi.setCEnq("999");
        ipi.setIpIntDTO(nt);

        return ipi;
    }
}
