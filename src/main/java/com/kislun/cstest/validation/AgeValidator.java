package com.kislun.cstest.validation;

import com.kislun.cstest.validation.annotation.Adult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.Period;

public class AgeValidator implements ConstraintValidator<Adult, LocalDate> {
    @Value("${project.age.validation}")
    int minAge;
    String message;

    @Override
    public void initialize(Adult constraintAnnotation) {
        this.message = constraintAnnotation.message();
        System.out.println("Initializing with message: " + message); // Для дебагу
    }

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext constraintValidatorContext) {
        if (dateOfBirth == null) {
            return true;
        }
        int currentAge = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (currentAge < minAge) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    message.replace("{minAge}", String.valueOf(minAge))
            ).addConstraintViolation();
            return false;
        }
        return true;
    }
}
