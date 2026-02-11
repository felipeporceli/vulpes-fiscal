package com.vulpesfiscal.demo.entities.enums;

public enum RegimeTributarioEmpresa {

    SIMPLES_NACIONAL(1),
    SIMPLES_EXCESSO_SUBLIMITE(2),
    REGIME_NORMAL(3);

    private final int codigo;

    RegimeTributarioEmpresa(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
