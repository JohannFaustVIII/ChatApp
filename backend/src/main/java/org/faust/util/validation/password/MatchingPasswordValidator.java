package org.faust.util.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MatchingPasswordValidator implements ConstraintValidator<MatchingPassword, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Field[] fields = value.getClass().getDeclaredFields();

        return Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Password.class))
                .map(field -> getValue(field, value))
                .collect(Collectors.toSet())
                .size() < 2;
    }

    private Object getValue(Field field, Object value) {
        boolean canAccess = field.canAccess(value);
        field.setAccessible(true);
        try {
            return field.get(value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } finally {
            field.setAccessible(canAccess);
        }
    }
}
