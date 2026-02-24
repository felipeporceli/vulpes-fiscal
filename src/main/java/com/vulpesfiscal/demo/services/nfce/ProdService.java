package com.vulpesfiscal.demo.services.nfce;

import com.vulpesfiscal.demo.controllers.dtos.nfce.det.DetDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.ProdDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.det.dfereferenciado.DfeReferenciadoDTO;
import com.vulpesfiscal.demo.entities.ItemVenda;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.entities.Venda;
import com.vulpesfiscal.demo.services.nfce.det.DfeReferenciadoService;
import com.vulpesfiscal.demo.services.nfce.det.ImpostoDevolService;
import com.vulpesfiscal.demo.services.nfce.det.ImpostoService;
import com.vulpesfiscal.demo.services.nfce.det.ObsItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdService {

    private final ImpostoService impostoService;
    private final ImpostoDevolService impostoDevolService;
    private final ObsItemService obsItemService;
    private final DfeReferenciadoService dfeReferenciadoService;

    public ProdDTO montarProduto(ItemVenda item) {

        Produto produto = item.getProduto();

        ProdDTO prod = new ProdDTO();

        prod.setCProd(produto.getIdProduto().toString());

        // Código de barras
        String ean = produto.getCodigoBarras();
        prod.setCEAN(ean != null ? ean : "SEM GTIN");
        prod.setCEANTrib(prod.getCEAN());

        prod.setXProd(produto.getDescricao());
        prod.setNCM(produto.getNcm().toString());

        // CFOP padrão NFC-e dentro do estado
        prod.setCFOP("5102");

        prod.setUCom(produto.getUnidade());
        prod.setQCom(item.getQuantidade());
        prod.setVUnCom(item.getValorUnitario());

        BigDecimal vProd = item.getQuantidade()
                .multiply(item.getValorUnitario());

        prod.setVProd(vProd);

        // Tributação igual à comercial
        prod.setUTrib(prod.getUCom());
        prod.setQTrib(prod.getQCom());
        prod.setVUnTrib(prod.getVUnCom());

        // Soma no total da nota
        prod.setIndTot(1);

        return prod;
    }

    public List<DetDTO> montarItens(Venda venda) {

        List<DetDTO> itens = new ArrayList<>();
        int nItem = 1;

        for (ItemVenda item : venda.getItens()) {

            DetDTO det = new DetDTO();
            det.setNItem(nItem++);
            det.setProd(montarProduto(item));
            det.setImposto(impostoService.gerarImposto());
            det.setImpostoDevol(impostoDevolService.gerarImpostoDevol());
            det.setObsItem(obsItemService.gerarObsItem());
            det.setDfeReferenciado(dfeReferenciadoService.gerarDfeReferenciado());

            itens.add(det);
        }

        return itens;
    }


}
