package ru.itmo.soa.vehicles.query;

import java.util.List;

public record VehicleQuery(List<FilterCondition> filters, List<SortOrder> sorts, int page, int size) {
}
