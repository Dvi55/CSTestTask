package com.kislun.cstest.validation.annotation;

import com.kislun.cstest.validation.AgeValidator;
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
    String message() default "Age must be at least {minAge}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
