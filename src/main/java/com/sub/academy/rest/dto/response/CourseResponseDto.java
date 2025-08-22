package com.sub.academy.rest.dto.response;

import com.sub.academy.entity.CourseType;

import java.util.Set;
import java.util.UUID;

public record CourseResponseDto(UUID id,
                                String name,
                                CourseType type,
                                Set<UUID> students,
                                Set<UUID> teachers) {
}
