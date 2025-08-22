package com.sub.academy.repository;

import com.sub.academy.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

    Page<Teacher> findAllByGroupAndCoursesId(String group, UUID courseId, Pageable pageable);

}
