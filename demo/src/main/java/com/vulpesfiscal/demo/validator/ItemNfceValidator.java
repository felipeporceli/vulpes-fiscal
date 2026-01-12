package com.vulpesfiscal.demo.validator;

import com.vulpesfiscal.demo.controllers.dtos.CadastroItemNfceDTO;
import com.vulpesfiscal.demo.controllers.mappers.ProdutoMapper;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.ItemNfce;
import com.vulpesfiscal.demo.entities.Produto;
import com.vulpesfiscal.demo.exceptions.*;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.repositories.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ItemNfceValidator {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper mapper;
    private static final BigDecimal QUANTIDADE_MAXIMA =
            new BigDecimal("99999999999"); // 11 dígitos



    public Produto validarCampos(CadastroItemNfceDTO dto) {
        if (dto.produtoId() == null) {
            throw new CampoInvalidoException("Produto", "Produto é obrigatório");
        }

        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        if (!produto.isAtivo()) {
            throw new RecursoInativoException("Produto está inativo");
        }

        if (dto.quantidade() == null) {
            throw new CampoInvalidoException("quantidade", "Quantidade é obrigatória");
        }

        if (dto.quantidade().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CampoInvalidoException("quantidade", "Quantidade deve ser maior que zero");
        }

        if (dto.quantidade().compareTo(QUANTIDADE_MAXIMA) > 0) {
            throw new CampoInvalidoException(
                    "quantidade",
                    "Quantidade excede o limite permitido"
            );
        }


        return produto;
    }
}

