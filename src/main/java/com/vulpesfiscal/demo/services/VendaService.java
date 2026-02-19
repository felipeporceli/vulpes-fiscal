package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.CadastroItemVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.VendaResponseDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.NfceDTO;
import com.vulpesfiscal.demo.controllers.mappers.NfceMapper;
import com.vulpesfiscal.demo.entities.*;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import com.vulpesfiscal.demo.exceptions.*;
import com.vulpesfiscal.demo.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final PagamentoService pagamentoService;
    private final ConsumidorRepository consumidorRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final NfceService nfceService;
    private final NfceMapper nfceMapper;
    private final NfceRepository nfceRepository;


    @Transactional
    public Venda criarVenda(
            CadastroVendaDTO dto,
            Integer empresaId,
            Integer estabelecimentoId
    ) {

        Venda venda = new Venda();

        // ===============================
        // ESTABELECIMENTO / EMPRESA
        // ===============================
        Estabelecimento estabelecimento = estabelecimentoRepository
                .findById(estabelecimentoId)
                .orElseThrow(() ->
                        new EstabelecimentoNaoEncontrado("Estabelecimento não encontrado"));

        if (!estabelecimento.getEmpresa().getId().equals(empresaId)) {
            throw new EmpresaDifereEstabelecimentoException(
                    "Estabelecimento não pertence à empresa informada");
        }

        Empresa empresa = estabelecimento.getEmpresa();

        venda.setEstabelecimento(estabelecimento);
        venda.setEmpresa(empresa);

        // ===============================
        // CONSUMIDOR
        // ===============================
        Consumidor consumidor = consumidorRepository
                .findById(dto.consumidorId())
                .orElseThrow(() ->
                        new ConsumidorNaoEncontradoException("Consumidor não encontrado"));

        venda.setConsumidor(consumidor);

        // ===============================
        // ITENS
        // ===============================
        if (dto.itens() == null || dto.itens().isEmpty()) {
            throw new CampoInvalidoException(
                    "itens",
                    "A venda deve possuir ao menos um item"
            );
        }

        BigDecimal totalVenda = BigDecimal.ZERO;
        List<ItemVenda> itensVenda = new ArrayList<>();

        for (CadastroItemVendaDTO itemDTO : dto.itens()) {

            Produto produto = produtoRepository
                    .findByEmpresaIdAndIdProduto(empresaId, itemDTO.idProduto())
                    .orElseThrow(() ->
                            new ProdutoNaoEncontradoException("Produto não encontrado"));

            ItemVenda item = new ItemVenda();
            item.setVenda(venda);
            item.setProduto(produto);
            item.setQuantidade(BigDecimal.valueOf(itemDTO.quantidade()));
            item.setCfop(itemDTO.cfop());

            item.setValorUnitario(produto.getPreco());

            BigDecimal totalItem = produto.getPreco()
                    .multiply(BigDecimal.valueOf(itemDTO.quantidade()));

            item.setValorTotal(totalItem);
            item.setEmpresa(empresa);
            item.setEstabelecimento(estabelecimento);

            totalVenda = totalVenda.add(totalItem);
            itensVenda.add(item);
        }

        venda.setItens(itensVenda);
        venda.setValorTotal(totalVenda);


        // ===============================
        // PAGAMENTO
        // ===============================
        if (dto.pagamento() == null) {
            throw new CampoInvalidoException(
                    "pagamento",
                    "Pagamento é obrigatório"
            );
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setMetodoPagamento(dto.pagamento().metodoPagamento());
        pagamento.setValorRecebido(dto.pagamento().valorRecebido());
        pagamento.setDesconto(dto.pagamento().desconto());

        pagamento.setValor(venda.getValorTotal());
        pagamentoService.processarPagamento(pagamento);
        pagamento.setVenda(venda);
        pagamento.setEmpresa(empresa);
        pagamento.setEstabelecimento(estabelecimento);

        pagamento.setStatusPagamento(StatusPagamento.CONCLUIDO);
        venda.setPagamento(pagamento);
        venda.setValorTotal(pagamento.getValorFinal());

        // ===============================
        // NFC-e
        // ===============================
        if (dto.emitirNfce() == null) {
            throw new CampoInvalidoException(
                    "emitirNfce",
                    "Informe se a NFC-e deve ser emitida"
            );
        }

        // Receber se irá emitir nota fiscal ou não.
        venda.setEmitirNfce(dto.emitirNfce());

        if (Boolean.TRUE.equals(dto.emitirNfce())) {
            System.out.println("NFC-e sendo emitida...");

            NfceDTO nfceDTO = nfceService.gerarNfce(venda, estabelecimentoId);
        }

        Venda vendaSalva = vendaRepository.save(venda);
        return vendaSalva;

    }
}