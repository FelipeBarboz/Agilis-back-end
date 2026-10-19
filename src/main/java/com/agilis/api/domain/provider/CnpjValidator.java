package com.agilis.api.domain.provider;

public final class CnpjValidator {

    private static final String PATTERN = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$";

    private CnpjValidator() {}

    public static String validate(String cnpj) {
        if (cnpj == null || !cnpj.matches(PATTERN)) {
            throw new IllegalArgumentException("Invalid CNPJ. Expected format: 00.000.000/0000-00");
        }
        return cnpj;
    }
}