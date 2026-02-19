package com.vulpesfiscal.demo.controllers.dtos.nfce;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record IdeDTO (

    // Cod uf do emitente (estabelecimento.uf)
    String cUF,

    // Gerado pela API
    String cNF,

    // "Venda de mercadoria"
    // "Venda a consumidor final"
    String natOp,

    // Modelo do documento: 65
    Integer mod,

    // 1
    Integer serie,

    // service.gerarProximonumero
    Integer nNF,

    // Data da emissao
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime dhEmi,

    // Data de saida
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime dhSaiEnt,

    // Tipo de operação
    // 1 - Saída (venda)
    // 0 - Entrada (compra)
    Integer tpNF,

    /* Destino da operação
    1 = Interna
    2 = Interestadual
    3 = Exterior
    NFC-e normalmente 1 */
    Integer idDest,

    // Código IBGE do município do emitente.
    String cMunFG,

    // Código IBGE do município do emitente. (por enquanto)
    String cMunFGIBS,


    // Tipo de Danfe. Valor 4 padrão.
    Integer tpImp,

    /* Tipo de emissão:
    1 - Normal
    2 - Contingência (Sefaz fora do ar) */
    Integer tpEmis,

    // Digito verificador da chave, gerado pela API.
    Integer cDV,

    /* 1 = Produção
    2 = Homologação */
    Integer tpAmb,

    /* 1 = Normal
    4 = Devolução */
    Integer finNFe,

    // Consumidor final: 1
    Integer indFinal,

    /*Presença do comprador:
    1 = Presencial
    4 = NFC-e com entrega*/
    Integer indPres,

    /* Intermediação
    0 = Sem intermediador
    1 = Marketplace*/
    Integer indIntermed,

    // Processo emissor: 0 - aplicativo do contribuinte
    Integer procEmi,

    // Versão do sistema emissor: VulpesFiscal 0.0.1 - Beta
    String verProc,

    /* Data/hora de entrada em contingência
    Só preenche se tpEmis = 9*/
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime dhCont,

    /*Justificativa da contingência
    Óbrigatório se houver contingência*/
    String xJust

    // Compra Governamental, Pagamento Antecipado, Nota Referenciada.
) {}

