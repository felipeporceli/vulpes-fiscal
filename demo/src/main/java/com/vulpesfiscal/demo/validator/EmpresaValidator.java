package com.vulpesfiscal.demo.validator;

import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.exceptions.CampoInvalidoException;
import com.vulpesfiscal.demo.exceptions.EmpresaComEstabelecimentoException;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmpresaValidator {

    private final EmpresaRepository repository;

    public void validar(Empresa empresa) {
        if (empresa.getCnpj() == null) {
            return;
        }

        if (repository.existsByCnpj(empresa.getCnpj())) {
            throw new CampoInvalidoException(
                    "cnpj",
                    "Já existe uma empresa cadastrada com este CNPJ"
            );
        }
    }

    public Empresa pesquisarPorCnpj(String cnpj) {
        return repository.findByCnpj(cnpj)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Empresa não encontrada para o CNPJ informado")
                );
    }

    public void validarDeletar (String cnpj) {
        Empresa empresa = repository.findByCnpj(cnpj)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Empresa não encontrada para o CNPJ informado"
                        )
                );

        if (!empresa.getEstabelecimentos().isEmpty()) {
            throw new EmpresaComEstabelecimentoException(
                    "Não é possível excluir a empresa pois existem estabelecimentos vinculados"
            );
        }
    }


}

