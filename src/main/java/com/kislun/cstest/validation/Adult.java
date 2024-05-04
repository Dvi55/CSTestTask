package com.kislun.cstest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Documented
@Constraint(validatedBy = AgeValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Adult {

    String message() default "Wrong age";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
