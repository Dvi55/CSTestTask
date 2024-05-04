package com.kislun.cstest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.Period;

public class AgeValidator implements ConstraintValidator<Adult, LocalDate> {
    @Value("${project.age.validation}")
    int minAge;


    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext constraintValidatorContext) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears() >= minAge;
    }
}
