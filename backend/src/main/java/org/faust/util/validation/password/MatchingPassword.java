package org.faust.util.validation.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MatchingPasswordValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchingPassword {

    String message() default "Passwords don't match!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
