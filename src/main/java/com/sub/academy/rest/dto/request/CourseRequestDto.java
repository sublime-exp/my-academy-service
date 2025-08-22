package com.sub.academy.rest.dto.request;

import com.sub.academy.entity.CourseType;
import com.sub.academy.rest.validation.AtLeastOneTeacher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

@AtLeastOneTeacher
public record CourseRequestDto(

        @Size(min = 10, max = 40, message = "Course name must be between 10 and 40 characters")
        @NotBlank(message = "Course name must not be blank")
        String name,

        @NotNull(message = "Course type is required")
        CourseType type,

        Set<@NotNull UUID> students,                 // existing students
        Set<@Valid StudentRequestDto> newStudents,   // students to create


        Set<@NotNull UUID> teachers,                // existing teachers

        @Valid
        @Size(min = 1, message = "New teachers must not be empty when provided")
        Set<@Valid TeacherRequestDto> newTeachers   // teachers to create
) {
}
