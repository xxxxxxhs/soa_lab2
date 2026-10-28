package ru.itmo.soa.vehicles.query;

public record FilterCondition(VehicleField field, FilterOperator operator, Comparable<?> value) {
}
