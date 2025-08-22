package com.sub.academy.domain.repository;

import com.sub.academy.domain.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
}
