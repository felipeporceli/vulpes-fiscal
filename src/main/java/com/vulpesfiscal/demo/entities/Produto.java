package com.vulpesfiscal.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produto")
@Data
@EntityListeners(AuditingEntityListener.class)
@ToString
public class Produto {


    @Column(name = "id_produto")
    private Integer idProduto;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id_tecnico")
    private Integer idTecnico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    @Column (name = "descricao", length = 300, nullable = false)
    private String descricao;

    @Column (name = "codigo_barras")
    private String codigoBarras;

    @Column (name = "qtd_estoque", nullable = false)
    private Integer qtdEstoque;

    @Column (name = "ncm", nullable = false)
    private String ncm;

    @Column (name = "cfop", nullable = false)
    private Integer cfop;

    @Column (name = "unidade", nullable = false)
    private String unidade;

    @Column (name = "preco", nullable = false)
    private BigDecimal preco;

    @Column (name = "ativo", nullable = false)
    private boolean ativo;

    @Column(name = "cest", length = 7)
    private String cest;

    @Column(name = "orig", nullable = false)
    private Integer orig; // 0..8


    // Campos de Auditoria
    @CreatedDate
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por")
    private Usuario usuario;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atualizado_por")
    private Usuario atualizadoPor;

}

