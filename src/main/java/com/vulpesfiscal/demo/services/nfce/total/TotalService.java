package com.vulpesfiscal.demo.services.nfce.total;

import com.vulpesfiscal.demo.controllers.dtos.nfce.total.TotalDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.total.icmstot.ICMSTotDTO;
import com.vulpesfiscal.demo.entities.ItemVenda;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TotalService {

    public TotalDTO gerarTotal(List<ItemVenda> itens) {

        BigDecimal vProd = BigDecimal.ZERO;
        BigDecimal vBC = BigDecimal.ZERO;
        BigDecimal vICMS = BigDecimal.ZERO;
        BigDecimal vFCP = BigDecimal.ZERO;

        for (ItemVenda item : itens) {

            BigDecimal totalItem = item.getValorUnitario()
                    .multiply(item.getQuantidade());

            vProd = vProd.add(totalItem);
            vBC = vBC.add(totalItem);

            BigDecimal icms = totalItem
                    .multiply(new BigDecimal("18"))
                    .divide(new BigDecimal("100"));

            vICMS = vICMS.add(icms);

            BigDecimal fcp = totalItem
                    .multiply(new BigDecimal("2"))
                    .divide(new BigDecimal("100"));

            vFCP = vFCP.add(fcp);
        }

        ICMSTotDTO icmsTot = new ICMSTotDTO();
        icmsTot.setVProd(vProd);
        icmsTot.setVBC(vBC);
        icmsTot.setVICMS(vICMS);
        icmsTot.setVFCP(vFCP);

        icmsTot.setVICMSDeson(BigDecimal.ZERO);
        icmsTot.setVBCST(BigDecimal.ZERO);
        icmsTot.setVST(BigDecimal.ZERO);

        icmsTot.setVFrete(BigDecimal.ZERO);
        icmsTot.setVSeg(BigDecimal.ZERO);
        icmsTot.setVDesc(BigDecimal.ZERO);
        icmsTot.setVOutro(BigDecimal.ZERO);

        icmsTot.setVNF(vProd);

        TotalDTO total = new TotalDTO();
        total.setIcmsTot(icmsTot);
        total.setIssqNtot(null);
        total.setRetTrib(null);
        total.setIsTot(null);
        total.setIbscbsTot(null);

        total.setVNFTot(vProd);

        return total;
    }
}
