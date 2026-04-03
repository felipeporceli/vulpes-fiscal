package com.vulpesfiscal.demo.repositories;

import com.vulpesfiscal.demo.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByClientId(String clientId);
}
