package com.vulpesfiscal.demo.services.nfce.det.imposto;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins.COFINSAliqDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins.COFINSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins.COFINSNTDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins.COFINSOutrDTO;
import com.vulpesfiscal.demo.entities.ItemVenda;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CofinsService {

    private static final BigDecimal CEM = new BigDecimal("100");

    public COFINSDTO gerarCofins(ItemVenda item) {

        RegimeTributarioEmpresa regime = item.getVenda() .getEmpresa() .getRegimeTributario();

        return switch (regime) {

            case REGIME_NORMAL ->
                    gerarRegimeNormal(item);

            case SIMPLES_NACIONAL ->
                    gerarSimplesNacional(item);

            case SIMPLES_EXCESSO_SUBLIMITE ->
                    gerarSimplesExcesso(item);

            default ->
                    throw new IllegalStateException(
                            "Regime tributário não suportado: " + regime
                    );
        };
    }

    private COFINSDTO gerarRegimeNormal(ItemVenda item) {

        BigDecimal base = calcularBase(item);
        BigDecimal aliquota = new BigDecimal("3.00");

        COFINSAliqDTO aliq = new COFINSAliqDTO();
        aliq.setCST("01");
        aliq.setVBC(base);
        aliq.setPCOFINS(aliquota);
        aliq.setVCOFINS(calcularPercentual(base, aliquota));

        COFINSDTO dto = new COFINSDTO();
        dto.setCofinsAliq(aliq);

        return dto;
    }

    private COFINSDTO gerarSimplesNacional(ItemVenda item) {

        BigDecimal base = calcularBase(item);

        COFINSOutrDTO outr = new COFINSOutrDTO();
        outr.setCST("49");
        outr.setVBC(base);
        outr.setPCOFINS(BigDecimal.ZERO);
        outr.setVCOFINS(BigDecimal.ZERO);

        COFINSDTO dto = new COFINSDTO();
        dto.setCofinsOutr(outr);

        return dto;
    }

    private COFINSDTO gerarSimplesExcesso(ItemVenda item) {

        COFINSNTDTO nt = new COFINSNTDTO();
        nt.setCst("04");

        COFINSDTO dto = new COFINSDTO();
        dto.setCofinsnt(nt);

        return dto;
    }

    private BigDecimal calcularBase(ItemVenda item) {
        return item.getValorTotal();
    }

    private BigDecimal calcularPercentual(BigDecimal base, BigDecimal aliquota) {

        return base.multiply(aliquota)
                .divide(CEM, 2, RoundingMode.HALF_UP);
    }

}
