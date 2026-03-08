package com.vulpesfiscal.demo.exceptions;

public class TributacaoNaoEncontradaParaUfException extends RuntimeException {

    public TributacaoNaoEncontradaParaUfException(String mensagem) {
        super(mensagem);
    }
}
