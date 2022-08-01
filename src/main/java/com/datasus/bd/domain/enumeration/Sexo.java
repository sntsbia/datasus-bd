package com.datasus.bd.domain.enumeration;

/**
 * The Sexo enumeration.
 */
public enum Sexo {
    F("Feminino"),
    M("Masculino");

    private final String value;

    Sexo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
