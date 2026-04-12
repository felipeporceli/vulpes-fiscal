package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.CadastroItemVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroVendaDTO;
import com.vulpesfiscal.demo.controllers.dtos.nfce.InfNFe;
import com.vulpesfiscal.demo.controllers.mappers.NfceMapper;
import com.vulpesfiscal.demo.entities.*;
import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import com.vulpesfiscal.demo.exceptions.*;
import com.vulpesfiscal.demo.repositories.*;
import com.vulpesfiscal.demo.security.SecurityService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final PagamentoService pagamentoService;
    private final PagamentoRepository pagamentoRepository;
    private final ConsumidorRepository consumidorRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final NfceService nfceService;
    private final NfceMapper nfceMapper;
    private final NfceRepository nfceRepository;
    private final SecurityService securityService;
    private final UsuarioRepository usuarioRepository;

    // Métodos de pagamento que permitem parcelamento
    private static final Set<MetodoPagamento> METODOS_PARCELAVEIS = Set.of(
            MetodoPagamento.CARTAO_CREDITO,
            MetodoPagamento.CREDITO_LOJA,
            MetodoPagamento.BOLETO
    );

    // Taxas de juros mensais por método de pagamento
    private static final Map<MetodoPagamento, BigDecimal> TAXAS_JUROS = Map.of(
            MetodoPagamento.CARTAO_CREDITO, new BigDecimal("0.0299"),
            MetodoPagamento.CREDITO_LOJA,   new BigDecimal("0.0350"),
            MetodoPagamento.BOLETO,         new BigDecimal("0.0199")
    );

    @Transactional
    public Venda criarVenda(CadastroVendaDTO dto,
                            Integer empresaId,
                            Integer estabelecimentoId) {

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

        // CONSUMIDOR
        Consumidor consumidor = consumidorRepository
                .findById(dto.consumidorId())
                .orElseThrow(() ->
                        new ConsumidorNaoEncontradoException("Consumidor não encontrado"));

        venda.setConsumidor(consumidor);

        // ITENS + BAIXA DE ESTOQUE

        if (dto.itens() == null || dto.itens().isEmpty()) {
            throw new CampoInvalidoException(
                    "itens",
                    "A venda deve possuir ao menos um item"
            );
        }

        BigDecimal totalVenda = BigDecimal.ZERO;
        List<ItemVenda> itensVenda = new ArrayList<>();
        List<Produto> produtosParaAtualizar = new ArrayList<>();

        for (CadastroItemVendaDTO itemDTO : dto.itens()) {
            Produto produto = produtoRepository
                    .findByEmpresaIdAndIdProduto(empresaId, itemDTO.idProduto())
                    .orElseThrow(() ->
                            new ProdutoNaoEncontradoException("Produto não encontrado: "
                                    + itemDTO.idProduto()));

            // Valida se tem estoque suficiente
            if (produto.getQtdEstoque() < itemDTO.quantidade()) {
                throw new CampoInvalidoException(
                        "quantidade",
                        "Estoque insuficiente para o produto: " + produto.getDescricao()
                                + ". Disponível: " + produto.getQtdEstoque()
                                + ", Solicitado: " + itemDTO.quantidade()
                );
            }

            // Faz a baixa no estoque
            produto.setQtdEstoque(produto.getQtdEstoque() - itemDTO.quantidade());
            produtosParaAtualizar.add(produto);

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

        // Salva todos os produtos com estoque atualizado
        produtoRepository.saveAll(produtosParaAtualizar);

        venda.setItens(itensVenda);
        venda.setValorTotal(totalVenda);

        // PAGAMENTO
        if (dto.pagamento() == null) {
            throw new CampoInvalidoException(
                    "pagamento",
                    "Pagamento é obrigatório"
            );
        }

        validarParcelas(dto);

        // Salva a venda primeiro para ter o id disponível para os pagamentos
        Venda vendaSalva = vendaRepository.save(venda);

        // Gera os registros de pagamento (1 por parcela)
        List<Pagamento> parcelas = gerarParcelas(dto, vendaSalva, empresa, estabelecimento, consumidor);
        pagamentoRepository.saveAll(parcelas);

        // Associa o primeiro pagamento à venda para manter compatibilidade
        vendaSalva.setPagamento(parcelas.get(0));
        vendaSalva.setValorTotal(parcelas.get(0).getValorFinal());
        vendaSalva.setParcelas(parcelas.size());


        // NFC-e
        if (dto.emitirNfce() == null) {
            throw new CampoInvalidoException(
                    "emitirNfce",
                    "Informe se a NFC-e deve ser emitida"
            );
        }

        vendaSalva.setEmitirNfce(dto.emitirNfce());

        if (Boolean.TRUE.equals(dto.emitirNfce())) {
            System.out.println("NFC-e sendo emitida...");
            InfNFe nfceDTO = nfceService.gerarNfce(vendaSalva, estabelecimentoId);
        }

        // Obter usuario logado
        String login = securityService.obterLoginUsuarioLogado();
        Usuario usuarioLogado = usuarioRepository.findByEmail(login);
        venda.setUsuario(usuarioLogado);


        return vendaRepository.save(vendaSalva);
    }

    // -------- MÉTODOS PRIVADOS --------

    private void validarParcelas(CadastroVendaDTO dto) {
        Integer parcelas = dto.pagamento().parcelas();
        MetodoPagamento metodo = dto.pagamento().metodoPagamento();

        if (parcelas != null && parcelas > 1) {
            if (!METODOS_PARCELAVEIS.contains(metodo)) {
                throw new CampoInvalidoException(
                        "parcelas",
                        "Parcelamento não permitido para o método de pagamento: "
                                + metodo.getDescricao()
                                + ". Métodos permitidos: Cartão de Crédito, Crédito Loja e Boleto."
                );
            }
        }
    }

    private BigDecimal calcularValorComJuros(BigDecimal valorFinal,
                                             MetodoPagamento metodo,
                                             int numeroParcelas) {
        // À vista não tem juros
        if (numeroParcelas <= 1) {
            return valorFinal;
        }

        BigDecimal taxa = TAXAS_JUROS.getOrDefault(metodo, BigDecimal.ZERO);

        // Juros compostos: valorFinal × (1 + taxa) ^ numeroParcelas
        BigDecimal fator = BigDecimal.ONE.add(taxa).pow(numeroParcelas);

        return valorFinal.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }

    private List<Pagamento> gerarParcelas(CadastroVendaDTO dto,
                                          Venda venda,
                                          Empresa empresa,
                                          Estabelecimento estabelecimento,
                                          Consumidor consumidor) {

        int numeroParcelas = dto.pagamento().parcelas() != null
                && dto.pagamento().parcelas() > 0
                ? dto.pagamento().parcelas()
                : 1;

        BigDecimal valorTotal = venda.getValorTotal();
        BigDecimal desconto = dto.pagamento().desconto() != null
                ? dto.pagamento().desconto()
                : BigDecimal.ZERO;

        BigDecimal valorSemJuros = valorTotal.subtract(desconto);
        BigDecimal valorComJuros = calcularValorComJuros(
                valorSemJuros,
                dto.pagamento().metodoPagamento(),
                numeroParcelas
        );

        BigDecimal valorParcela = valorComJuros
                .divide(BigDecimal.valueOf(numeroParcelas), 2, RoundingMode.HALF_UP);

        LocalDate hoje = LocalDate.now();
        List<Pagamento> listaParcelas = new ArrayList<>();
        MetodoPagamento metodo = dto.pagamento().metodoPagamento();

        for (int i = 0; i < numeroParcelas; i++) {
            Pagamento pagamento = new Pagamento();
            pagamento.setMetodoPagamento(metodo);
            pagamento.setValor(valorTotal);
            pagamento.setDesconto(desconto);
            pagamento.setValorFinal(valorComJuros);
            pagamento.setValorParcela(valorParcela);
            pagamento.setValorRecebido(dto.pagamento().valorRecebido());
            pagamento.setParcelas(numeroParcelas);
            pagamento.setParcelaAtual((i + 1) + "/" + numeroParcelas);
            pagamento.setVenda(venda);
            pagamento.setEmpresa(empresa);
            pagamento.setEstabelecimento(estabelecimento);
            pagamento.setConsumidor(consumidor);

            // Cartão de crédito: todas as parcelas CONCLUIDO
            // Demais métodos: primeira CONCLUIDO, restantes PENDENTE
            if (metodo == MetodoPagamento.CARTAO_CREDITO || i == 0) {
                pagamento.setStatusPagamento(StatusPagamento.CONCLUIDO);
            } else {
                pagamento.setStatusPagamento(StatusPagamento.PENDENTE);
            }

            // Primeira parcela vence hoje, demais vencem dia 20 dos meses seguintes
            if (i == 0) {
                pagamento.setDataVencimento(hoje);
            } else {
                pagamento.setDataVencimento(
                        hoje.plusMonths(i).withDayOfMonth(20)
                );
            }

            listaParcelas.add(pagamento);
        }

        return listaParcelas;
    }
}