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
}
