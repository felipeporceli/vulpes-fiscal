package com.vulpesfiscal.demo.services.nfce.det.imposto;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pis.PISAliqDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pis.PISDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pis.PISNTDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.pis.PISOutrDTO;
import com.vulpesfiscal.demo.entities.ItemVenda;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.util.Objects.requireNonNull;

@Service
public class PISService {

    private static final BigDecimal CEM = new BigDecimal("100");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    // Exemplo comum (Lucro Presumido): 0,65%
    private static final BigDecimal ALIQUOTA_PIS_PRESUMIDO = new BigDecimal("0.65");

    public PISDTO gerarPis(ItemVenda item) {
        requireNonNull(item, "ItemVenda não pode ser nulo");

        RegimeTributarioEmpresa regime = obterRegime(item);
        BigDecimal base = calcularBaseItem(item);

        PISDTO pis = new PISDTO();

        switch (regime) {

            case REGIME_NORMAL:
                // CST 01 = operação tributável com alíquota básica (mais comum)
                pis.setPisAliq(gerarPisAliq(base, ALIQUOTA_PIS_PRESUMIDO, "01"));
                break;

            case SIMPLES_NACIONAL:
                // Simples normalmente usa CST 49 em PISOutr com valores zerados
                pis.setPisOutr(gerarPisOutr(base, ZERO, "49"));
                break;

            case SIMPLES_EXCESSO_SUBLIMITE:
                // Exemplo: monofásico/revenda → CST 04 (não tributado)
                // (se no seu caso excesso de sublimite não for isso, troque a regra)
                pis.setPisnt(gerarPisNT("04"));
                break;

            default:
                throw new IllegalArgumentException("Regime tributário não suportado: " + regime);
        }

        return pis;
    }

    // ----------------------------
    // Builders por “grupo” do XML
    // ----------------------------

    private PISAliqDTO gerarPisAliq(BigDecimal vBC, BigDecimal pPIS, String cst) {
        validarNaoNegativo(vBC, "vBC");
        validarNaoNegativo(pPIS, "pPIS");

        BigDecimal vPIS = percentual(vBC, pPIS);

        PISAliqDTO dto = new PISAliqDTO();
        dto.setCST(cst);
        dto.setVBC(scale2(vBC));
        dto.setPPIS(scale2(pPIS));
        dto.setVPIS(scale2(vPIS));

        return dto;
    }

    private PISOutrDTO gerarPisOutr(BigDecimal vBC, BigDecimal pPIS, String cst) {
        validarNaoNegativo(vBC, "vBC");
        validarNaoNegativo(pPIS, "pPIS");

        BigDecimal vPIS = percentual(vBC, pPIS);

        PISOutrDTO dto = new PISOutrDTO();
        dto.setCST(cst);
        dto.setVBC(scale2(vBC));
        dto.setPPIS(scale2(pPIS));
        dto.setVPIS(scale2(vPIS));

        return dto;
    }

    private PISNTDTO gerarPisNT(String cst) {
        if (cst == null || cst.isBlank()) {
            throw new IllegalArgumentException("CST PIS NT inválido");
        }
        PISNTDTO dto = new PISNTDTO();
        dto.setCST(cst);
        return dto;
    }

    // ----------------------------
    // Utilitários
    // ----------------------------

    private RegimeTributarioEmpresa obterRegime(ItemVenda item) {
        return item.getVenda()
                .getEmpresa()
                .getRegimeTributario();
    }

    /**
     * Base padrão por item: valorUnitario * quantidade.
     * Se você já tem total do item (com desconto/rateio), pode trocar para item.getValorTotal().
     */
    private BigDecimal calcularBaseItem(ItemVenda item) {
        BigDecimal qtd = requireNonNull(item.getQuantidade(), "quantidade não pode ser nula");
        BigDecimal vUn = requireNonNull(item.getValorUnitario(), "valorUnitario não pode ser nulo");

        BigDecimal base = vUn.multiply(qtd);

        return base.max(ZERO);
    }

    private BigDecimal percentual(BigDecimal base, BigDecimal aliquota) {
        return base.multiply(aliquota)
                .divide(CEM, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal scale2(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private void validarNaoNegativo(BigDecimal v, String nome) {
        if (v == null || v.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(nome + " inválido: " + v);
        }
    }
}

