package com.sub.academy.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record StudentRequestDto(

        @NotBlank(message = "Student name must not be blank")
        String name,

        @NotNull(message = "Student age is required")
        Integer age,

        @NotBlank(message = "Student group must not be blank")
        String group,

        Set<UUID> courses
) {
}
