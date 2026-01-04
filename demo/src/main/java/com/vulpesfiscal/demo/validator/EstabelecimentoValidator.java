package com.vulpesfiscal.demo.validator;

import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.exceptions.CampoInvalidoException;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstabelecimentoValidator {

    private final EstabelecimentoRepository repository;

    public void validar(Estabelecimento Estabelecimento) {
        if (Estabelecimento.getCnpj() == null) {
            return;
        }

        if (repository.existsByCnpj(Estabelecimento.getCnpj())) {
            throw new CampoInvalidoException(
                    "cnpj",
                    "Já existe uma Estabelecimento cadastrada com este CNPJ"
            );
        }
    }

    public Estabelecimento pesquisarPorCnpj(String cnpj) {
        return repository.findByCnpj(cnpj)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Estabelecimento não encontrado para o CNPJ informado")
                );
    }
}

