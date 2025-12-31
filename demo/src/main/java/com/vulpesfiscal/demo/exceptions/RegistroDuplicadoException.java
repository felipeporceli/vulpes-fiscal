package com.vulpesfiscal.demo.exceptions;

// Exception personalizada para lançamento de exceção quando um objeto já estiver armazenado no banco.
public class RegistroDuplicadoException extends RuntimeException {
    public RegistroDuplicadoException(String message) {
        super(message);
    }
}
