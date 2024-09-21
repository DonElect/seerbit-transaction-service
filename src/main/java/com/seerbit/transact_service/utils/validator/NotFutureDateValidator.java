package com.seerbit.transact_service.utils.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class NotFutureDateValidator implements ConstraintValidator<NotFutureDate, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        return !date.isAfter(LocalDateTime.now());
    }
}

