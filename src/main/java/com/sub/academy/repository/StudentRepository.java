package com.sub.academy.repository;

import com.sub.academy.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {

    Page<Student> findAllByGroupAndCoursesId(String group, UUID courseId, Pageable pageable);

    Page<Student> findAllByCoursesId(UUID courseId, Pageable pageable);

    Page<Student> findAllByAgeGreaterThanAndCoursesId(Integer age, UUID courseId, Pageable pageable);

    Page<Student> findAllByGroup(String group, Pageable pageable);
}
