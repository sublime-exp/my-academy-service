package com.sub.academy.rest.validation;

import com.sub.academy.rest.dto.request.CourseRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneTeacherValidator implements ConstraintValidator<AtLeastOneTeacher, CourseRequestDto> {

    @Override
    public boolean isValid(CourseRequestDto dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true; // handled elsewhere
        boolean hasIds = dto.teachers() != null && !dto.teachers().isEmpty();
        boolean hasNews = dto.newTeachers() != null && !dto.newTeachers().isEmpty();
        return hasIds || hasNews;
    }
}