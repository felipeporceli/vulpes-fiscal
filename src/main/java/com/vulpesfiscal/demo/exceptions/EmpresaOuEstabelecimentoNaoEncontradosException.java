package com.vulpesfiscal.demo.exceptions;

public class EmpresaOuEstabelecimentoNaoEncontradosException extends RuntimeException {

    public EmpresaOuEstabelecimentoNaoEncontradosException(String mensagem) {
        super(mensagem);
    }
}
