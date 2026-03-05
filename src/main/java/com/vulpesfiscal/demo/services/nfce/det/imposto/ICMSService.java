package com.vulpesfiscal.demo.services.nfce.det.imposto;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.cofins.COFINSDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.imposto.icms.*;
import com.vulpesfiscal.demo.entities.ItemVenda;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
@RequiredArgsConstructor
public class ICMSService {
    private static final BigDecimal CEM = new BigDecimal("100");
    private static final BigDecimal ZERO = new BigDecimal("0");

    private ObterRegimeService obterRegimeService;

    public ICMSDTO gerarIcms (ItemVenda item) {
        RegimeTributarioEmpresa regime = item.getVenda() .getEmpresa() .getRegimeTributario();


        return switch (regime) {


            case SIMPLES_NACIONAL ->
                    gerarSimplesNacional(item);

            case REGIME_NORMAL ->
                    gerarRegimeNormal(item);


            default ->
                    throw new IllegalStateException(
                            "Regime tributário não suportado: " + regime
                    );
        };
        }

    private ICMSDTO gerarSimplesNacional (ItemVenda item) {
        String possuiSt = "Sim";

        ICMSDTO dto = new ICMSDTO();

        if (possuiSt == "Não") {
            ICMSSN500DTO icms500DTO = new ICMSSN500DTO();

            icms500DTO.setOrig(0);
            icms500DTO.setCSOSN("500");
            icms500DTO.setVBCSTRet(new BigDecimal("0.00"));
            icms500DTO.setPST(new BigDecimal("0.00"));
            icms500DTO.setVICMSSTRet(new BigDecimal("0.00"));


            dto.setIcmssn500DTO(icms500DTO);
            return dto;

        }
        else {
            ICMSSN102DTO icmssn102DTO = new ICMSSN102DTO();

            icmssn102DTO.setCSOSN("102");
            icmssn102DTO.setOrig(0);

            dto.setIcmssn102DTO(icmssn102DTO);
            return dto;
        }

    }

    private ICMSDTO gerarRegimeNormal (ItemVenda item) {
        ICMSDTO dto = new ICMSDTO();
        dto.setIcms00DTO(gerarIcms00(item, new BigDecimal("18"), new BigDecimal("2.00")));
        return dto;
    }

    public ICMS00DTO gerarIcms00(ItemVenda item, BigDecimal aliquotaIcms, BigDecimal pFcpOpcional) {
        Produto produto = item.getProduto();

        BigDecimal vBC = calcularBaseIcms(item); // vProd - desconto (+ rateios se houver)

        ICMS00DTO dto = new ICMS00DTO();
        dto.setOrig(0);   // 0..8
        dto.setCst("00");
        dto.setModBC(3);
        dto.setVBC(vBC);
        dto.setPICMS(aliquotaIcms);

        BigDecimal vIcms = vBC.multiply(aliquotaIcms)
                .divide(CEM, 2, RoundingMode.HALF_UP);
        dto.setVICMS(vIcms);

        if (pFcpOpcional != null && pFcpOpcional.compareTo(BigDecimal.ZERO) > 0) {
            dto.setPFCP(pFcpOpcional);
            BigDecimal vFcp = vBC.multiply(pFcpOpcional)
                    .divide(CEM, 2, RoundingMode.HALF_UP);
            dto.setVFCP(vFcp);
        }

        return dto;
    }


    //Testar ICMS20 e ICMS60
    public ICMS20DTO gerarICMS20(BigDecimal valorTotal, BigDecimal pRedBC, BigDecimal pICMS,
                                 BigDecimal pFCP) {

        BigDecimal fatorReducao = CEM.subtract(pRedBC);
        BigDecimal vBC = valorTotal
                .multiply(fatorReducao)
                .divide(CEM, 2, RoundingMode.HALF_UP);

        BigDecimal vICMS = vBC
                .multiply(pICMS)
                .divide(CEM, 2, RoundingMode.HALF_UP);

        ICMS20DTO dto = new ICMS20DTO();
        dto.setOrig(0);
        dto.setCST("20");
        dto.setModBC(3);
        dto.setPRedBC(pRedBC);
        dto.setVBC(vBC);
        dto.setPICMS(pICMS);
        dto.setVICMS(vICMS);

        if (pFCP != null && pFCP.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal vFCP = vBC
                    .multiply(pFCP)
                    .divide(CEM, 2, RoundingMode.HALF_UP);

            dto.setPFCP(pFCP);
            dto.setVFCP(vFCP);
        }

        return dto;
    }


    public ICMS60DTO gerarICMS60(
            Integer orig,
            BigDecimal quantidade,
            BigDecimal stUnitario,
            BigDecimal stTotal,
            BigDecimal baseStTotal,
            BigDecimal pSt,
            BigDecimal pFcpSt,
            BigDecimal baseFcpStTotal
    ) {
        validarObrigatorios(orig, quantidade);

        // Regra mínima prática: para CST 60, você deveria informar ao menos vICMSSTRet
        BigDecimal vIcmsStRet = calcularVICMSSTRet(quantidade, stUnitario, stTotal);

        ICMS60DTO dto = new ICMS60DTO();
        dto.setOrig(orig);
        dto.setCST("60");

        // Campos mais usados/aceitos
        dto.setVICMSSTRet(vIcmsStRet);

        // Opcionais (se você tiver fonte confiável)
        if (baseStTotal != null && baseStTotal.compareTo(ZERO) > 0) {
            dto.setVBCSTRet(scale2(baseStTotal));
        }
        if (pSt != null && pSt.compareTo(ZERO) > 0) {
            dto.setPST(scale2(pSt));
        }

        // FCP ST (só se existir na sua UF/produto e você tiver os dados)
        if (pFcpSt != null && pFcpSt.compareTo(ZERO) > 0) {
            dto.setPFCPSTRet(scale2(pFcpSt));

            BigDecimal baseFcp = (baseFcpStTotal != null && baseFcpStTotal.compareTo(ZERO) > 0)
                    ? baseFcpStTotal
                    : baseStTotal; // fallback comum: mesma base do ST

            if (baseFcp != null && baseFcp.compareTo(ZERO) > 0) {
                dto.setVBCFCPSTRet(scale2(baseFcp));

                BigDecimal vFcp = baseFcp.multiply(pFcpSt)
                        .divide(CEM, 2, RoundingMode.HALF_UP);

                dto.setVFCPSTRet(scale2(vFcp));
            }
        }

        return dto;
    }

    private void validarObrigatorios(Integer orig, BigDecimal quantidade) {
        if (orig == null || orig < 0 || orig > 8) {
            throw new IllegalArgumentException("orig inválido (0..8): " + orig);
        }
        if (quantidade == null || quantidade.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("quantidade deve ser > 0");
        }
    }

    /**
     * Se você tiver ST unitário, calcula: stUnitario * quantidade
     * Se você tiver ST total direto, usa ele.
     * Se não tiver nenhum, lança erro (porque CST 60 sem vICMSSTRet fica inconsistente).
     */
    private BigDecimal calcularVICMSSTRet(BigDecimal quantidade, BigDecimal stUnitario, BigDecimal stTotal) {
        if (stTotal != null && stTotal.compareTo(ZERO) >= 0) {
            return scale2(stTotal);
        }
        if (stUnitario != null && stUnitario.compareTo(ZERO) >= 0) {
            return scale2(stUnitario.multiply(quantidade));
        }
        throw new IllegalStateException(
                "Para ICMS60 (ST anterior), informe stTotal ou stUnitario para montar vICMSSTRet."
        );
    }

    private BigDecimal scale2(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularBaseIcms(ItemVenda item) {
        BigDecimal vProd = item.getValorTotal(); // ou item.getVProd()
        BigDecimal desconto = BigDecimal.ZERO;
        return vProd.subtract(desconto).max(BigDecimal.ZERO);
    }

    }



