package ru.itmo.soa.vehicles.query;

import java.util.Arrays;
import java.util.Optional;

public enum FilterOperator {
    EQ("eq"),
    NE("ne"),
    GT("gt"),
    GTE("gte"),
    LT("lt"),
    LTE("lte");

    private final String token;

    FilterOperator(String token) {
        this.token = token;
    }

    public static Optional<FilterOperator> fromToken(String token) {
        return Arrays.stream(values())
                .filter(operator -> operator.token.equals(token))
                .findFirst();
    }

    public String token() {
        return token;
    }

    public boolean isEquality() {
        return this == EQ || this == NE;
    }
}
