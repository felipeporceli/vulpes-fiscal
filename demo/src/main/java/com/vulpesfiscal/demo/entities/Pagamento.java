package com.vulpesfiscal.demo.entities;

import com.vulpesfiscal.demo.entities.enums.MetodoPagamento;
import com.vulpesfiscal.demo.entities.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "metodo_pagamento")
    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodoPagamento;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "troco")
    private BigDecimal troco;

    @Column(name = "parcelas")
    private Integer parcelas;

    @Column(name = "status_pagamento")
    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "estabelecimento_id")
    private Estabelecimento estabelecimento;

    // --------------- AUDITORIA ---------------

    @CreatedDate
    @Column (name = "criado_em")
    private LocalDateTime dataCriacao;

    private Integer criadoPor;

    @LastModifiedDate
    @Column (name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    private Integer atualizadoPor;

}
