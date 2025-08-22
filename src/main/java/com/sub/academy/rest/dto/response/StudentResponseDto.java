package com.sub.academy.rest.dto.response;

import java.util.Set;
import java.util.UUID;

public record StudentResponseDto(UUID id,
                                 String name,
                                 Integer age,
                                 String group,
                                 Set<UUID> courses) {
}
