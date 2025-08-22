package com.sub.academy.rest.dto.request;

import com.sub.academy.entity.CourseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record CoursePatchDto(

        @Size(min = 10, max = 40, message = "Course name must be between 10 and 40 characters")
        String name,

        CourseType type,

        Set<@NotNull UUID> students,                 // existing students
        Set<@Valid StudentRequestDto> newStudents,   // students to create

        @Size(min = 1, message = "At least one teacher (existing or new) must be provided")
        Set<@NotNull UUID> teachers,

        @Valid
        @Size(min = 1, message = "At least one teacher (existing or new) must be provided")
        Set<@Valid TeacherRequestDto> newTeachers   // teachers to create
) {}