package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.entities.*;
import com.vulpesfiscal.demo.exceptions.*;
import com.vulpesfiscal.demo.repositories.ConsumidorRepository;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import com.vulpesfiscal.demo.repositories.ProdutoRepository;
import com.vulpesfiscal.demo.repositories.VendaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final PagamentoService pagamentoService;
    private final ConsumidorRepository consumidorRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final NfceService nfceService;

    @Transactional
    public Venda criarVenda(Venda venda, Integer empresaId, Integer estabelecimentoId) {

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findById(estabelecimentoId)
                .orElseThrow(() -> new EstabelecimentoNaoEncontrado("Estabelecimento não encontrado"));

        if (!estabelecimento.getEmpresa().getId().equals(empresaId)) {
            throw new EmpresaDifereEstabelecimentoException("Estabelecimento não pertence à empresa informada");
        }

        venda.setEstabelecimento(estabelecimento);
        Empresa empresa = estabelecimento.getEmpresa();
        venda.setEmpresa(empresa);

        Consumidor consumidor = consumidorRepository
                .findById(venda.getConsumidor().getId())
                .orElseThrow(() ->
                        new ConsumidorNaoEncontradoException("Consumidor não encontrado")
                );

        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new CampoInvalidoException("Item", "A venda deve possuir ao menos um item");
        }

        BigDecimal totalVenda = BigDecimal.ZERO;

        for (ItemVenda item : venda.getItens()) {

            item.setVenda(venda);

            Produto produto = produtoRepository.findByEmpresaIdAndIdProduto(empresaId, item.getProduto().getIdProduto())
                    .orElseThrow(() -> new ProdutoNaoEncontradoException("idProduto não encontrado"));

            item.setProduto(produto);
            item.setValorUnitario(produto.getPreco());

            BigDecimal totalItem = item.getValorUnitario()
                    .multiply(BigDecimal.valueOf(item.getQuantidade()));

            item.setValorTotal(totalItem);
            totalVenda = totalVenda.add(totalItem);
            item.setEstabelecimento(estabelecimento);
            item.setEmpresa(estabelecimento.getEmpresa());

        }

        if (venda.getDesconto() != null
                && venda.getDesconto().compareTo(BigDecimal.ZERO) < 0) {
            throw new CampoInvalidoException(
                    "desconto",
                    "O desconto não pode ser negativo"
            );
        }

        if (venda.getDesconto() != null) {
            totalVenda = totalVenda.subtract(venda.getDesconto());
        }

        venda.setValorTotal(totalVenda);

        // ---------- PAGAMENTO ----------
        Pagamento pagamento = venda.getPagamento();

        if (pagamento == null) {
            throw new CampoInvalidoException("Pagamento", "Pagamento é obrigatório");
        }

        pagamentoService.processarPagamento(pagamento, totalVenda);
        pagamento.setVenda(venda);
        pagamento.setEmpresa(empresa);
        pagamento.setEstabelecimento(estabelecimento);
        venda.setPagamento(pagamento);

        // ------------- NFCE ---------------

        if (venda.getEmitirNfce() == null) {
            throw new CampoInvalidoException(
                    "emitirNfce",
                    "Informe se a NFC-e deve ser emitida"
            );
        }

        if (Boolean.TRUE.equals(venda.getEmitirNfce())) {
            Nfce nfce = nfceService.emitirNfceSeNecessario(venda);
        }

        return vendaRepository.save(venda);
    }
}
