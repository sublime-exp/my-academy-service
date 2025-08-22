package com.sub.academy.service.student;

import com.sub.academy.entity.Student;
import com.sub.academy.rest.dto.request.StudentRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentService {

    long count();

    Student create(StudentRequestDto student);

    Student read(UUID id);

    Student update(UUID id, StudentRequestDto student);

    void delete(UUID id);

    Page<Student> findByCourseId(UUID courseId, Pageable pageable);

    Page<Student> findByGroup(String group, Pageable pageable);

    Page<Student> findStudentsByCourseIdAndGroup(UUID courseId, String group, Pageable pageable);

    Page<Student> findByAgeGreaterThanAndCourseId(Integer age, UUID courseId, Pageable pageable);
}
