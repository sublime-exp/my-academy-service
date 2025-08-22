package com.sub.academy.rest.exception;

import java.util.UUID;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(UUID id) {
        super("Student " + id + " not found");
    }
}
