package com.vulpesfiscal.demo.validator;

import com.vulpesfiscal.demo.entities.Consumidor;
import com.vulpesfiscal.demo.entities.Empresa;
import com.vulpesfiscal.demo.exceptions.CampoInvalidoException;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.ConsumidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsumidorValidator {

    private final ConsumidorRepository repository;

    public void validar(Consumidor Consumidor) {
        if (Consumidor.getCpf() == null) {
            return;
        }

        if (repository.existsByCpf(Consumidor.getCpf())) {
            throw new CampoInvalidoException(
                    "cpf",
                    "Já existe uma Consumidor cadastrado com este CPF"
            );
        }
    }

    public Consumidor pesquisarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Consumidor não encontrado para o Id informado")
                );
    }

    public void validarDeletar (Integer id) {
        Consumidor Consumidor = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Consumidor não encontrada para o id informado"
                        )
                );
    }


}

