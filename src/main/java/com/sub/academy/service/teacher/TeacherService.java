package com.sub.academy.service.teacher;

import com.sub.academy.entity.Teacher;
import com.sub.academy.rest.dto.request.TeacherRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TeacherService {

    long count();

    Teacher create(Teacher teacher);

    Teacher read(UUID id);

    Teacher update(UUID id, TeacherRequestDto teacher);

    void delete(UUID id);

    Page<Teacher> findTeachersByCourseIdAndGroup(UUID courseId, String group, Pageable pageable);

}
