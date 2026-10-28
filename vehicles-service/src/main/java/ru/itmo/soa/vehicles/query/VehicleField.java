package ru.itmo.soa.vehicles.query;

import jakarta.persistence.criteria.Path;
import ru.itmo.soa.vehicles.model.VehicleType;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public enum VehicleField {
    ID("id", Integer::valueOf),
    NAME("name", value -> value),
    COORDINATES_X("coordinates.x", Integer::valueOf),
    COORDINATES_Y("coordinates.y", Long::valueOf),
    CREATION_DATE("creationDate", OffsetDateTime::parse),
    ENGINE_POWER("enginePower", Long::valueOf),
    CAPACITY("capacity", Numbers::parseDouble),
    DISTANCE_TRAVELLED("distanceTravelled", Numbers::parseFloat),
    TYPE("type", VehicleType::valueOf),
    NUMBER_OF_WHEELS("numberOfWheels", Integer::valueOf);

    private final String apiName;

    private final Function<String, Comparable<?>> parser;

    VehicleField(String apiName, Function<String, Comparable<?>> parser) {
        this.apiName = apiName;
        this.parser = parser;
    }

    public static Optional<VehicleField> fromApiName(String apiName) {
        return Arrays.stream(values())
                .filter(field -> field.apiName.equals(apiName))
                .findFirst();
    }

    public String apiName() {
        return apiName;
    }

    public Comparable<?> parse(String value) {
        return parser.apply(value);
    }

    public boolean supports(FilterOperator operator) {
        return this != TYPE || operator.isEquality();
    }

    public <T> Path<T> path(Path<?> root) {
        Path<?> path = root;
        for (String attribute : apiName.split("\\.")) {
            path = path.get(attribute);
        }
        @SuppressWarnings("unchecked")
        Path<T> typed = (Path<T>) path;
        return typed;
    }
}
