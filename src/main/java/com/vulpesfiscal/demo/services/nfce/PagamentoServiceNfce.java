package com.vulpesfiscal.demo.services.nfce;

import com.vulpesfiscal.demo.controllers.dtos.nfce.pagamento.PagamentoDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.pagamento.card.CardDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.pagamento.detpag.DetPagDTO;
import com.vulpesfiscal.demo.entities.Pagamento;
import com.vulpesfiscal.demo.entities.Venda;
import com.vulpesfiscal.demo.services.PagamentoService;
import com.vulpesfiscal.demo.services.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.vulpesfiscal.demo.entities.enums.MetodoPagamento.CARTAO_CREDITO;
import static com.vulpesfiscal.demo.entities.enums.MetodoPagamento.CARTAO_DEBITO;

@Service
@RequiredArgsConstructor
public class PagamentoServiceNfce {

    private final PagamentoService pagamentoService;

    public PagamentoDTO gerarPagamento(Venda venda) {

        Pagamento pagamento = venda.getPagamento();
        PagamentoDTO pagamentoNfe = new PagamentoDTO();
        DetPagDTO detPag = new DetPagDTO();

        if (pagamento.getParcelas() != null) {
            detPag.setIndPag(1);
        }

        detPag.setTPag(pagamento.getMetodoPagamento().getCodigoSefaz());
        detPag.setXPag(pagamento.getMetodoPagamento().getDescricao());
        detPag.setIndPag(pagamento.getMetodoPagamento().getIndPag());
        detPag.setDPag(LocalDate.now());
        detPag.setCNPJPag(null);
        detPag.setUFPag(venda.getEmpresa().getUf());


        if (pagamento.getMetodoPagamento() == CARTAO_CREDITO ||
                pagamento.getMetodoPagamento() == CARTAO_DEBITO ) {
            CardDTO card = new CardDTO();
            card.setCnpjReceb(venda.getConsumidor().getCnpj());
            card.setTpIntegra(2);
            card.setCnpj(venda.getEmpresa().getCnpj());

        }

        pagamentoNfe.setDetPag(detPag);
        pagamentoNfe.setTroco(pagamento.getTroco());
    return pagamentoNfe;
    }

}
