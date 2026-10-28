package ru.itmo.soa.vehicles.query;

import java.util.regex.Pattern;

public final class Numbers {

    private static final Pattern DECIMAL = Pattern.compile("[-+]?\\d+(\\.\\d+)?([eE][-+]?\\d+)?");

    private Numbers() {
    }

    public static double parseDouble(String value) {
        if (!DECIMAL.matcher(value).matches()) {
            throw new NumberFormatException(value);
        }
        double result = Double.parseDouble(value);
        if (!Double.isFinite(result)) {
            throw new NumberFormatException(value);
        }
        return result;
    }

    public static float parseFloat(String value) {
        if (!DECIMAL.matcher(value).matches()) {
            throw new NumberFormatException(value);
        }
        float result = Float.parseFloat(value);
        if (!Float.isFinite(result)) {
            throw new NumberFormatException(value);
        }
        return result;
    }
}
