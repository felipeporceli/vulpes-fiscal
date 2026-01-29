package com.vulpesfiscal.demo.entities;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "consumidor")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Consumidor {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "nome", nullable = false)
    private String nome;

    @Column (name = "cpf", nullable = false)
    private String cpf;

    @Column (name = "email")
    private String email;

}
