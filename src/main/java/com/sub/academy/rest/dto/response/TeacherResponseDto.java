package com.sub.academy.rest.dto.response;

import java.util.Set;
import java.util.UUID;

public record TeacherResponseDto(UUID id,
                                 String name,
                                 String group,
                                 Set<UUID> courses) {
}