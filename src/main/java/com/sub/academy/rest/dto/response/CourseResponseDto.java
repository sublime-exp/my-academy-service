package com.sub.academy.rest.dto;

import com.sub.academy.entity.CourseType;

import java.util.Set;
import java.util.UUID;

public record CourseDto(UUID id,
                        String name,
                        CourseType type,
                        Set<UUID> studentsIds,
                        Set<UUID> teachersIds) {
}
