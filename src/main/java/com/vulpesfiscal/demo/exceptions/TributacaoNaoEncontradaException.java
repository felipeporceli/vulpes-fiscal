package com.vulpesfiscal.demo.exceptions;

public class TributacaoNaoEncontradaException extends RuntimeException {

    public TributacaoNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
