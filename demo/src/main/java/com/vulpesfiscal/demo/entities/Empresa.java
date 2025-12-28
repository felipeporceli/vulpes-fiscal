package com.vulpesfiscal.demo.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table (name = "empresa")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Empresa {

    @Id
    @Column (name = "id")
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "razao_social", length = 300, nullable = false)
    private String razaoSocial;

    @Column (name = "nome_fantasia", length = 100, nullable = true)
    private String nomeFantasia;

    @Column (name = "cnpj", length = 14, nullable = false)
    private String cnpj;

    @Column (name = "inscricao_estadual", length = 14, nullable = false)
    private String inscricaoEstadual;

    @Column (name = "regime_tributario", length = 30, nullable = false)
    private String regimeTributario;

    @Column (name = "ambiente_sefaz", length = 50, nullable = false)
    private String ambienteSefaz;

    @Enumerated (EnumType.STRING)
    @Column (name = "status", length = 7, nullable = false)
    private StatusEmpresa statusEmpresa;

    @Column(name = "data_abertura", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dataAbertura;

    @CreatedDate
    @Column (name = "criado_em")
    private LocalDateTime dataCriacao;

    private Integer criadoPor;

    @LastModifiedDate
    @Column (name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    private Integer atualizadoPor;
}
