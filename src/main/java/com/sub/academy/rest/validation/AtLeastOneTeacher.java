package com.sub.academy.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneTeacherValidator.class)
@Documented
public @interface AtLeastOneTeacher {
    String message() default "At least one teacher (existing or new) must be provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}