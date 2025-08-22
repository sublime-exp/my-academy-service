package com.sub.academy.service.course;

import com.sub.academy.entity.Course;
import com.sub.academy.entity.CourseType;
import com.sub.academy.rest.dto.request.CoursePatchDto;
import com.sub.academy.rest.dto.request.CourseRequestDto;

import java.util.UUID;

public interface CourseService {

    long countByType(CourseType type);

    Course create(CourseRequestDto course);

    Course read(UUID id);

    Course update(UUID id, CourseRequestDto course);

    Course patch(UUID id, CoursePatchDto course);

    void delete(UUID id);
}
