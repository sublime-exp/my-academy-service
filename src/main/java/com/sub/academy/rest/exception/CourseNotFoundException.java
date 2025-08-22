package com.sub.academy.rest.exception;

import java.util.UUID;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(UUID id) {
        super("Course " + id + " not found");
    }
}