package com.sub.academy.repository;

import com.sub.academy.entity.Course;
import com.sub.academy.entity.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    Long countByType(CourseType type);

}
