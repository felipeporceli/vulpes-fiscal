package com.vulpesfiscal.demo.entities;

import com.vulpesfiscal.demo.entities.enums.StatusEmpresa;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table (name = "estabelecimento")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Estabelecimento {

    @Id
    @Column (name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false) // não pode existir sem empresa
    @JoinColumn(
            name = "empresa_id",      // nome da FK no banco
            nullable = false
    )
    private Empresa empresa;

    @OneToMany(mappedBy = "estabelecimento")
    private List<Produto> produtos;

    @Column (name = "nome_fantasia", length = 100, nullable = true)
    private String nomeFantasia;

    @Column (name = "cnpj", length = 14, nullable = false)
    private String cnpj;

    @Column (name = "telefone", length = 20, nullable = true)
    private String telefone;

    @Column (name = "email", length = 100, nullable = true)
    private String email;

    @Column (name = "inscricao_estadual", length = 14, nullable = false)
    private String inscricaoEstadual;

    @Column (name = "logradouro", length = 100, nullable = false)
    private String logradouro;

    @Column (name = "cidade", length = 50, nullable = false)
    private String cidade;

    @Column (name = "estado", length = 2, nullable = false)
    private String estado;

    @Enumerated (EnumType.STRING)
    @Column (name = "status", length = 7, nullable = false)
    private StatusEmpresa status;

    @Column (name = "matriz", nullable = false)
    private boolean matriz;

    @Column (name = "inscricao_municipal", length = 50, nullable = true)
    private String inscricaoMunicipal;

    @Column (name = "numero")
    private String numero;

    @Column (name = "complemento")
    private String complemento;

    @Column (name = "bairro")
    private String bairro;

    @Column (name = "municipio_id")
    private String municipioId;

    @Column (name = "cep")
    private String cep;

    @Column (name = "pais_id")
    private String paisId;

    @Column (name = "pais")
    private String pais;

    @Column (name = "uf_id")
    private String codUf;

    // Auditoria
    @Column(name = "data_abertura", nullable = false)
    private LocalDate dataAbertura;

    @CreatedDate
    @Column (name = "criado_em")
    private LocalDateTime dataCriacao;

    private Integer criadoPor;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    private Integer atualizadoPor;







}
