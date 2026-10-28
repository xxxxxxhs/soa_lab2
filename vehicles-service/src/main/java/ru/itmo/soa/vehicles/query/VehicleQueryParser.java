package ru.itmo.soa.vehicles.query;

import jakarta.ws.rs.core.MultivaluedMap;
import ru.itmo.soa.vehicles.error.ApiException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VehicleQueryParser {

    private static final Pattern FILTER = Pattern.compile("([^\\[\\]=]+)\\[([^\\[\\]=]+)\\]=(.+)");

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private VehicleQueryParser() {
    }

    public static VehicleQuery parse(MultivaluedMap<String, String> params) {
        int page = parsePage(params.getFirst("page"));
        int size = parseSize(params.getFirst("size"));
        List<SortOrder> sorts = parseSort(params.get("sort"));
        List<FilterCondition> filters = parseFilters(params.get("filter"));
        return new VehicleQuery(filters, sorts, page, size);
    }

    private static int parsePage(String raw) {
        if (raw == null) {
            return DEFAULT_PAGE;
        }
        int page = parseInt("page", raw);
        if (page < 1) {
            throw ApiException.unprocessable("page: значение должно быть не меньше 1");
        }
        return page;
    }

    private static int parseSize(String raw) {
        if (raw == null) {
            return DEFAULT_SIZE;
        }
        int size = parseInt("size", raw);
        if (size < 1 || size > MAX_SIZE) {
            throw ApiException.unprocessable("size: значение должно быть от 1 до " + MAX_SIZE);
        }
        return size;
    }

    private static int parseInt(String name, String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw ApiException.badRequest(name + ": ожидается целое число");
        }
    }

    private static List<SortOrder> parseSort(List<String> values) {
        List<SortOrder> sorts = new ArrayList<>();
        if (values == null) {
            return sorts;
        }
        Set<VehicleField> used = EnumSet.noneOf(VehicleField.class);
        for (String value : values) {
            for (String token : value.split(",", -1)) {
                boolean descending = token.startsWith("-");
                String name = descending ? token.substring(1) : token;
                VehicleField field = VehicleField.fromApiName(name)
                        .orElseThrow(() -> ApiException.badRequest("sort: неизвестное поле сортировки '" + token + "'"));
                if (!used.add(field)) {
                    throw ApiException.badRequest("sort: поле '" + name + "' указано несколько раз");
                }
                sorts.add(new SortOrder(field, descending));
            }
        }
        return sorts;
    }

    private static List<FilterCondition> parseFilters(List<String> values) {
        List<FilterCondition> filters = new ArrayList<>();
        if (values == null) {
            return filters;
        }
        for (String value : values) {
            filters.add(parseFilter(value));
        }
        return filters;
    }

    private static FilterCondition parseFilter(String raw) {
        Matcher matcher = FILTER.matcher(raw);
        if (!matcher.matches()) {
            throw ApiException.badRequest("filter: условие '" + raw + "' не соответствует формату поле[оператор]=значение");
        }
        String fieldName = matcher.group(1);
        String operatorToken = matcher.group(2);
        String rawValue = matcher.group(3);

        VehicleField field = VehicleField.fromApiName(fieldName)
                .orElseThrow(() -> ApiException.badRequest("filter: неизвестное поле '" + fieldName + "'"));
        FilterOperator operator = FilterOperator.fromToken(operatorToken)
                .orElseThrow(() -> ApiException.badRequest("filter: неизвестный оператор '" + operatorToken + "'"));

        if (!field.supports(operator)) {
            throw ApiException.unprocessable("filter: оператор '" + operator.token()
                    + "' неприменим к полю '" + field.apiName() + "'");
        }
        try {
            return new FilterCondition(field, operator, field.parse(rawValue));
        } catch (RuntimeException e) {
            throw ApiException.unprocessable("filter: значение '" + rawValue
                    + "' не соответствует типу поля '" + field.apiName() + "'");
        }
    }
}
