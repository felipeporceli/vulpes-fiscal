package com.vulpesfiscal.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "nfce",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_nfce_chave_acesso", columnNames = "chave_acesso")
        }
)
@Data
@EntityListeners(AuditingEntityListener.class)
public class Nfce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /* ================= RELACIONAMENTOS ================= */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

   /* @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;*/

    @Column (name = "usuario_id")
    private Integer usuario;

    /* ================= DADOS FISCAIS ================= */

    @Column
    private Integer numero;

    @Column
    private Integer serie;

    @Column(name = "chave_acesso", unique = true)
    private String chaveAcesso;

    @Column(name = "data_emissao")
    private LocalDateTime dataEmissao;

    @Column(name = "valor_total", precision = 15, scale = 4)
    private BigDecimal valorTotal;

    @Column
    private String status;

    @Column(name = "protocolo_autorizacao")
    private String protocoloAutorizacao;

    /* ================= ITENS ================= */

    @OneToMany(
            mappedBy = "nfce",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemNfce> itens = new ArrayList<>();

    /* ================= AUDITORIA ================= */

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "criado_por")
    private Integer criadoPor;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "atualizado_por")
    private Integer atualizadoPor;
}
