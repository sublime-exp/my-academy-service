package com.sub.academy.rest.dto;

import java.util.Set;
import java.util.UUID;

public record TeacherDto(UUID id,
                         String name,
                         String group,
                         Set<UUID> courseIds) {
}