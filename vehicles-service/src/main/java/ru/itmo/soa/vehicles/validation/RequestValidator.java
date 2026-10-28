package ru.itmo.soa.vehicles.validation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import ru.itmo.soa.vehicles.error.ApiException;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class RequestValidator {

    @Inject
    Validator validator;

    public void validate(Object body) {
        Set<ConstraintViolation<Object>> violations = validator.validate(body);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .sorted()
                    .collect(Collectors.joining("; "));
            throw ApiException.unprocessable(message);
        }
    }
}
