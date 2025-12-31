package com.vulpesfiscal.demo.validator;

import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.exceptions.CampoInvalidoException;
import com.vulpesfiscal.demo.exceptions.EmpresaNaoEncontradaException;
import com.vulpesfiscal.demo.exceptions.RegistroDuplicadoException;
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
                        new EmpresaNaoEncontradaException("Empresa não encontrada para o CNPJ informado")
                );
    }
}

