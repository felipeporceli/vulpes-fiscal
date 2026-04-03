package com.vulpesfiscal.demo.exceptions;

public class PagamentoNaoEncontradoException extends RuntimeException {

    public PagamentoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
