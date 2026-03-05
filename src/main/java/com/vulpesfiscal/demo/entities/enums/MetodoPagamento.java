package com.vulpesfiscal.demo.entities.enums;

public enum MetodoPagamento {

    DINHEIRO("01", "Dinheiro", 0),
    CHEQUE("02", "Cheque", 0),
    CARTAO_CREDITO("03", "Cartão de Crédito", 0),
    CARTAO_DEBITO("04", "Cartão de Débito", 0),
    CREDITO_LOJA("05", "Crédito Loja", 1),
    VALE_ALIMENTACAO("10", "Vale Alimentação", 0),
    VALE_REFEICAO("11", "Vale Refeição", 0),
    VALE_PRESENTE("12", "Vale Presente", 0),
    VALE_COMBUSTIVEL("13", "Vale Combustível", 0),
    BOLETO("15", "Boleto Bancário", 1),
    PIX("17", "PIX", 0),
    TRANSFERENCIA_BANCARIA("18", "Transferência Bancária", 0),
    CARTEIRA_DIGITAL("19", "Carteira Digital", 0),
    SEM_PAGAMENTO("90", "Sem Pagamento", 0),
    OUTROS("99", "Outros", 0);

    private final String codigoSefaz;
    private final String descricao;
    private final Integer indPag;

    MetodoPagamento(String codigoSefaz, String descricao, Integer indPag) {
        this.codigoSefaz = codigoSefaz;
        this.descricao = descricao;
        this.indPag = indPag;
    }

    public String getCodigoSefaz() {
        return codigoSefaz;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getIndPag() {
        return indPag;
    }
}
