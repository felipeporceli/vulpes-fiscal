package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.controllers.dtos.CadastroItemNfceDTO;
import com.vulpesfiscal.demo.controllers.dtos.CadastroNfceDTO;
import com.vulpesfiscal.demo.controllers.mappers.ItemNfceMapper;
import com.vulpesfiscal.demo.controllers.mappers.NfceMapper;
import com.vulpesfiscal.demo.entities.*;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.*;
import com.vulpesfiscal.demo.validator.ItemNfceValidator;
import com.vulpesfiscal.demo.validator.NfceValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class NfceService {

    private final NfceRepository nfceRepository;
    private final EmpresaRepository empresaRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ProdutoRepository produtoRepository;
    private final NfceMapper nfceMapper;
    private final ItemNfceMapper itemNfceMapper;
    private final ItemNfceValidator itemNfceValidator;

    public Nfce gerarNfce(
            Integer empresaId,
            Integer estabelecimentoId,
            Integer usuarioId,
            CadastroNfceDTO dto
    ) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada"));

        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estabelecimento não encontrado"));

        Integer serie = dto.serie() != null ? dto.serie() : 1;
        Integer ultimoNumero = nfceRepository.buscarUltimoNumero(
                empresaId,
                estabelecimentoId,
                serie
        );
        Integer numero = ultimoNumero + 1;
        Nfce nfce = nfceMapper.toEntity(
                dto,
                empresa,
                estabelecimento,
                usuarioId,
                numero,
                serie
        );
        BigDecimal valorTotalNfce = BigDecimal.ZERO;

        for (CadastroItemNfceDTO itemDto : dto.itens()) {
            Produto produto = itemNfceValidator.validarCampos(itemDto);

            ItemNfce item = itemNfceMapper.toEntity(itemDto);

            item.setProduto(produto);
            item.setNfce(nfce);
            item.setNcm(produto.getNcm());
            item.setCfop(produto.getCfop());
            item.setValorUnitario(produto.getPreco());

            BigDecimal totalItem =
                    produto.getPreco().multiply(item.getQuantidade());

            item.setValorTotal(totalItem);

            nfce.getItens().add(item);
            valorTotalNfce = valorTotalNfce.add(totalItem);
        }

        nfce.setValorTotal(valorTotalNfce);

        return nfceRepository.save(nfce);
    }
}
