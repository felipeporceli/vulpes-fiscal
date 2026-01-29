package com.vulpesfiscal.demo.exceptions;

public class EstabelecimentoNaoEncontrado extends RuntimeException {

    public EstabelecimentoNaoEncontrado(String mensagem) {
        super(mensagem);
    }
}

