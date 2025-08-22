package com.sub.academy.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.UUID;

public record TeacherRequestDto(

        @NotBlank(message = "Teacher name must not be blank")
        String name,

        @NotBlank(message = "Teacher group must not be blank")
        String group,

        Set<UUID> courses
) {
}
