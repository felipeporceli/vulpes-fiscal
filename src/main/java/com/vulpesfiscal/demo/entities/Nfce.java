package com.vulpesfiscal.demo.entities;

import com.vulpesfiscal.demo.entities.enums.StatusNfce;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.List;

@Entity
@Table(
        name = "nfce",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_nfce_empresa_estabelecimento_numero",
                        columnNames = {"empresa_id", "estabelecimento_id", "numero"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Nfce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Uma empresa pode ter várias NFC-e
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Um estabelecimento pode ter várias NFC-e
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    // Um usuário pode emitir várias NFC-e
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Número sequencial por empresa + estabelecimento
     * A lógica de incremento será feita no Service
     */
    @Column(nullable = false)
    private String numero;

    /**
     * Série padrão = 1
     */
    @Column(nullable = false)
    private Integer serie = 1;

    /**
     * Pode ser nulo (preenchido após autorização)
     */
    @Column(name = "chave_acesso")
    private String chaveAcesso;

    /**
     * Valor total da venda vinculada à NFC-e
     */
    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusNfce statusNfce;

    /**
     * Pode ser nulo
     */
    @Column(name = "protocolo_autorizacao")
    private String protocoloAutorizacao;

    @Column(name = "data_emissao", updatable = false)
    private OffsetDateTime dataEmissao;

    // ===== AUDITORIA =====

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "criado_por")
    private Integer criadoPor;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "atualizado_por")
    private Integer atualizadoPor;
}

