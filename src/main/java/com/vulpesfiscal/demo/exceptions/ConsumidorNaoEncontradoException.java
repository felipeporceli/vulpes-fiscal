package com.vulpesfiscal.demo.exceptions;

public class ConsumidorNaoEncontradoException extends RuntimeException {

    public ConsumidorNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
