package com.vulpesfiscal.demo.exceptions;

public class EmpresaNaoEncontradaException extends RuntimeException {

    public EmpresaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
