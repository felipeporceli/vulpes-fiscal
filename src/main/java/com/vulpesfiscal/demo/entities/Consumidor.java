package com.vulpesfiscal.demo.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Table(
        name = "consumidor",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_consumidor_empresa_cpf",
                        columnNames = {"empresa_id", "cpf"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Consumidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cpf;

    @Column
    private String email;

    /* ======================
       RELACIONAMENTOS INVERSOS
       ====================== */

    @OneToMany(mappedBy = "consumidor", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Venda> vendas;

    @Column (name = "cnpj")
    private String cnpj;

    @Column (name = "estrangeiro_id")
    private String estrangeiroId;

    @Column (name = "inscricao_estadual")
    private String inscricaoEstadual;

    @Column (name = "indicador_inscricao")
    private String indicadorInscricao;

    @Column (name = "inscricao_suframa")
    private String inscricaoSuframa;

    @Column (name = "inscricao_municipal")
    private String inscricaoMunicipal;

    @Column (name = "logradouro")
    private String logradouro;

    @Column (name = "numero")
    private String numero;

    @Column (name = "complemento")
    private String complemento;

    @Column (name = "bairro")
    private String bairro;

    @Column (name = "municipio_id")
    private String municipioId;

    @Column (name = "municipio")
    private String municipio;

    @Column (name = "uf")
    private String uf;

    @Column (name = "cep")
    private String cep ;

    @Column (name = "pais_id")
    private String paisId;

    @Column (name = "pais")
    private String pais ;

    @Column (name = "telefone")
    private String telefone;





}
