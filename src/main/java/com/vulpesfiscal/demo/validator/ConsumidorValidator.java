package com.vulpesfiscal.demo.validator;

import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.entities.Estabelecimento;
import com.vulpesfiscal.demo.exceptions.CampoInvalidoException;
import com.vulpesfiscal.demo.exceptions.EstabelecimentoNaoEncontrado;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.ConsumidorRepository;
import com.vulpesfiscal.demo.repositories.EmpresaRepository;
import com.vulpesfiscal.demo.repositories.EstabelecimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsumidorValidator {

    private final ConsumidorRepository repository;
    private final EmpresaRepository empresaRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    public void validar(Consumidor consumidor, Integer empresaId, Integer estabelecimentoId) {
        if (consumidor.getCpf() == null) {
            throw new CampoInvalidoException(
                    "cpf",
                    "CPF nao pode ser nulo"
            );
        }

        if (repository.existsByCpfAndEmpresaId(consumidor.getCpf(), consumidor.getEmpresa().getId())) {
            throw new CampoInvalidoException(
                    "cpf",
                    "Já existe uma Consumidor cadastrado com este CPF"
            );
        }

        if (!estabelecimentoRepository.existsByIdAndEmpresaId(estabelecimentoId, empresaId)) {
            throw new RecursoNaoEncontradoException(
                    "Estabelecimento ou empresa não encontrados"
            );
        }
    }

    public Consumidor pesquisarPorCpfEempresa(String cpf,
                                             Integer empresaId) {
        return repository.findByCpfAndEmpresaId(cpf, empresaId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Consumidor não encontrado para o CPF informado")
                );
    }

    public Consumidor validarDeletar(String cpf) {
        return repository.findByCpf(cpf)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Consumidor não encontrado para o CPF informado"
                        )
                );
    }


}

