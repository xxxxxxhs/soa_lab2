package ru.itmo.soa.vehicles.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FiniteValidator.class)
public @interface Finite {

    String message() default "значение должно быть конечным числом";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
