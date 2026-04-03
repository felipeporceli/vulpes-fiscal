package com.vulpesfiscal.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table (name = "client")
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "redirect_uri")
    private String redirectURI;

    @Column(name = "scope")
    private String scope;

}
