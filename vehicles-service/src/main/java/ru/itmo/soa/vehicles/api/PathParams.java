package ru.itmo.soa.vehicles.api;

import ru.itmo.soa.vehicles.error.ApiException;
import ru.itmo.soa.vehicles.model.VehicleType;
import ru.itmo.soa.vehicles.query.Numbers;

import java.util.Arrays;
import java.util.stream.Collectors;

final class PathParams {

    private PathParams() {
    }

    static int parseId(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw ApiException.badRequest("id: ожидается целое число");
        }
    }

    static float parseDistance(String raw) {
        try {
            return Numbers.parseFloat(raw);
        } catch (NumberFormatException e) {
            throw ApiException.badRequest("distanceTravelled: ожидается число");
        }
    }

    static VehicleType parseType(String raw) {
        try {
            return VehicleType.valueOf(raw);
        } catch (IllegalArgumentException e) {
            String allowed = Arrays.stream(VehicleType.values()).map(Enum::name).collect(Collectors.joining(", "));
            throw ApiException.badRequest("type: неизвестное значение, допустимые: " + allowed);
        }
    }
}
