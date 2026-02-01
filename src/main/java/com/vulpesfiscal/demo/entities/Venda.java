package com.vulpesfiscal.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "venda")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // OneToOne
    @OneToOne(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pagamento pagamento;

    // OneToOne
    @OneToOne
    @JoinColumn(name = "nfce_id")
    private Nfce nfce;


    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens;


    // ManyToOne
    @ManyToOne
    @JoinColumn(name = "consumidor_id", nullable = false)
    private Consumidor consumidor;

    // ManyToOne
    @ManyToOne
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Column(name = "desconto")
    private BigDecimal desconto;

    @Column(name = "parcelas")
    private Integer parcelas;

    @Transient
    private Boolean emitirNfce;

    @CreatedDate
    @Column(name = "criado_em")
    private LocalDateTime dataCriacao;

    private Integer criadoPor;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    private Integer atualizadoPor;

}

