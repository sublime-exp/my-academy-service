package com.sub.academy.rest.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Validation failed");
        detail.setType(URI.create("https://example.com/problems/validation-error"));
        detail.setProperty("timestamp", OffsetDateTime.now());

        //field errors: { "field": "message", ... }
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        //(class-level) errors
        for (ObjectError ge : ex.getBindingResult().getGlobalErrors()) {
//            fieldErrors.put("__global", ge.getDefaultMessage());
            fieldErrors.put("teachers", ge.getDefaultMessage()); // if it’s always about teachers
        }

        detail.setProperty("errors", fieldErrors);
        return detail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Constraint violation");
        detail.setType(URI.create("https://example.com/problems/constraint-violation"));
        detail.setProperty("timestamp", OffsetDateTime.now());

        List<String> violations = ex.getConstraintViolations().stream()
                .map(this::formatViolation)
                .toList();
        detail.setProperty("errors", violations);
        return detail;
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ProblemDetail handleCourseNotFound(CourseNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle("Resource not found");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("https://example.com/problems/not-found"));
        detail.setProperty("timestamp", OffsetDateTime.now());
        return detail;
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleStudentNotFound(StudentNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle("Resource not found");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("https://example.com/problems/not-found"));
        detail.setProperty("timestamp", OffsetDateTime.now());
        return detail;
    }

    @ExceptionHandler(TeacherNotFoundException.class)
    public ProblemDetail handleTeacherNotFound(TeacherNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle("Resource not found");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("https://example.com/problems/not-found"));
        detail.setProperty("timestamp", OffsetDateTime.now());
        return detail;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(ResponseStatusException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        detail.setTitle("Request error");
        detail.setType(URI.create("https://example.com/problems/request-error"));
        detail.setProperty("timestamp", OffsetDateTime.now());
        return detail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("Data integrity violation");
        detail.setDetail("Operation violates database constraints.");
        detail.setType(URI.create("https://example.com/problems/data-integrity"));
        detail.setProperty("timestamp", OffsetDateTime.now());
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOther(Exception ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("Unexpected error");
        detail.setDetail("An unexpected error occurred.");
        detail.setType(URI.create("https://example.com/problems/internal-error"));
        detail.setProperty("timestamp", OffsetDateTime.now());
        return detail;
    }

    private String formatViolation(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + ": " + violation.getMessage();
    }
}
