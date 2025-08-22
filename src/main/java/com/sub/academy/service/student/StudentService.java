package com.sub.academy.service;

import com.sub.academy.entity.Student;
import com.sub.academy.rest.dto.request.StudentRequestDto;

import java.util.List;
import java.util.UUID;

public interface StudentService {

    long count();

    Student create(StudentRequestDto student);

    Student read(UUID id);

    Student update(UUID id, StudentRequestDto student);

    void delete(UUID id);

    List<Student> findByCourseId(UUID courseId);

    List<Student> findByGroup(String group);

    List<Student> findStudentsByCourseIdAndGroup(UUID courseId, String group);

    List<Student> findByAgeGreaterThanAndCourseId(Integer age, UUID courseId);
}
