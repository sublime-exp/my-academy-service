package com.sub.academy.service;

import com.sub.academy.entity.Teacher;
import com.sub.academy.rest.dto.request.TeacherRequestDto;

import java.util.List;
import java.util.UUID;

public interface TeacherService {

    long count();

    Teacher create(Teacher teacher);

    Teacher read(UUID id);

    Teacher update(UUID id, TeacherRequestDto teacher);

    void delete(UUID id);

    List<Teacher> findTeachersByCourseIdAndGroup(UUID courseId, String group);

}
