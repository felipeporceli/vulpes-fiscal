package com.vulpesfiscal.demo.services;

import com.vulpesfiscal.demo.entities.Client;
import com.vulpesfiscal.demo.exceptions.RecursoNaoEncontradoException;
import com.vulpesfiscal.demo.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Client salvar(Client client) {
        var senhaCriptografa = passwordEncoder.encode(client.getClientSecret());
        client.setClientSecret(senhaCriptografa);
        return repository.save(client);
    }

    public Client obterPorId(String id) {
        try {
            return repository.findByClientId(id);
        } catch (NumberFormatException e) {
            return null; // 👈 se não for número, retorna null sem estourar exceção
        }
    }


}
