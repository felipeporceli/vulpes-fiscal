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

    @Column (name = "codigo_barras", nullable = false)
    private String codigoBarras;

    @Column (name = "qtd_estoque", nullable = false)
    private Integer qtdEstoque;

    @Column (name = "ncm", nullable = false)
    private Integer ncm;

    @Column (name = "cfop", nullable = false)
    private Integer cfop;

    @Column (name = "unidade", nullable = false)
    private String unidade;

    @Column (name = "preco", nullable = false)
    private BigDecimal preco;

    @Column (name = "ativo", nullable = false)
    private boolean ativo;

    @CreatedDate
    @Column (name = "criado_em")
    private LocalDateTime dataCriacao;

    private Integer criadoPor;

    @LastModifiedDate
    @Column (name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    private Integer atualizadoPor;

}

