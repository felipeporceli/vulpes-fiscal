package com.vulpesfiscal.demo.entities;

import com.vulpesfiscal.demo.entities.enums.RegimeTributarioEmpresa;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produto_tributacao")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ProdutoTributacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "nome")
    private String nome;

    @Column(name = "uf", length = 2, nullable = false)
    private String uf;

    @Column(name = "cfop", length = 4)
    private String cfop;

    @Column(name = "cst_icms")
    private String cstIcms;

    @Column(name = "csosn_icms")
    private String csosnIcms;

    @Column(name = "aliquota_icms")
    private BigDecimal aliquotaIcms;

    @Column(name = "p_fcp")
    private BigDecimal pFcp;

    @Column(name = "p_red_bc")
    private BigDecimal pRedBc;

    @Column(name = "tem_st_anterior")
    private Boolean temStAnterior;

    @Column(name = "cst_pis")
    private String cstPis;

    @Column(name = "aliquota_pis")
    private BigDecimal aliquotaPis;

    @Column(name = "cst_cofins")
    private String cstCofins;

    @Column(name = "regime_tributario")
    @Enumerated(EnumType.STRING)
    private RegimeTributarioEmpresa regimeTributarioEmpresa;

    @Column(name = "aliquota_cofins")
    private BigDecimal aliquotaCofins;


    @CreatedDate
    @Column (name = "criado_em")
    private LocalDateTime dataCriacao;

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
