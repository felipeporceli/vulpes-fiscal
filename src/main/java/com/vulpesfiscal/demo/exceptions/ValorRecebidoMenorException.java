package com.vulpesfiscal.demo.exceptions;

import lombok.Getter;

public class ValorRecebidoMenorException extends RuntimeException {
    @Getter
    private String campo;

    public ValorRecebidoMenorException(String campo, String mensagem) {
            super(mensagem);
            this.campo = campo;
        }
}
