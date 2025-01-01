package com.example.util;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidationUtil {
    
    private static final ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
    private static final Validator v = vf.getValidator();

    private ValidationUtil(){}

    public static <T> void validate(T object){
        Set<ConstraintViolation<T>> violations = v.validate(object);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<T> violation : violations) {
                errorMessage.append(violation.getPropertyPath())
                            .append(" ")
                            .append(violation.getMessage())
                            .append("; ");
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }
}
