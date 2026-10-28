package ru.itmo.soa.shop.api;

import ru.itmo.soa.shop.error.ApiException;

final class PathParams {

    private PathParams() {
    }

    static int parseVehicleId(String raw) {
        return parseInt("vehicle-id", raw);
    }

    static int parseNumberOfWheels(String raw) {
        int value = parseInt("number-of-wheels", raw);
        if (value < 1) {
            throw ApiException.unprocessable("number-of-wheels: значение должно быть больше 0");
        }
        return value;
    }

    private static int parseInt(String name, String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw ApiException.badRequest(name + ": ожидается целое число");
        }
    }
}
