package com.sub.academy.rest.exception;

import java.util.UUID;

public class TeacherNotFoundException extends RuntimeException {
    public TeacherNotFoundException(UUID id) {
        super("Teacher " + id + " not found");
    }
}
