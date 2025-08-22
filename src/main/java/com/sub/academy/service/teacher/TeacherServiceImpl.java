package com.sub.academy.service;

import com.sub.academy.entity.Course;
import com.sub.academy.entity.Teacher;
import com.sub.academy.repository.CourseRepository;
import com.sub.academy.repository.TeacherRepository;
import com.sub.academy.rest.dto.request.TeacherRequestDto;
import com.sub.academy.rest.exception.TeacherNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;

    private final CourseRepository courseRepository;

    @Override
    public Teacher create(Teacher teacher) {
        return repository.save(teacher);
    }

    @Override
    public Teacher read(UUID id) {
        return repository.findById(id).orElseThrow(() -> new TeacherNotFoundException(id));
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    @Transactional
    public Teacher update(UUID id, TeacherRequestDto dto) {
        Teacher existing = repository.findById(id).orElseThrow(() -> new TeacherNotFoundException(id));

        existing.setName(dto.name());
        existing.setGroup(dto.group());

        if (dto.courses() != null) {
            Set<Course> targetCourses = new HashSet<>(courseRepository.findAllById(dto.courses()));
            Set<Course> currentCourses = new HashSet<>(existing.getCourses());

            // Remove courses no longer present
            for (Course c : currentCourses) {
                if (!targetCourses.contains(c)) {
                    existing.removeCourse(c);
                }
            }

            // Add new ones
            for (Course c : targetCourses) {
                if (!existing.getCourses().contains(c)) {
                    existing.addCourse(c);
                }
            }
        }

        return existing;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.findById(id)
                .ifPresent(teacher -> {
                    teacher.deleteCourses();
                    repository.delete(teacher);
                });
    }

    @Override
    public List<Teacher> findTeachersByCourseIdAndGroup(UUID courseId, String group) {
        return repository.findAllByGroupAndCoursesId(group, courseId);
    }
}
