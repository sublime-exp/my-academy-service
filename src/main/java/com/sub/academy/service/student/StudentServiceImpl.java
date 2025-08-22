package com.sub.academy.service;

import com.sub.academy.entity.Course;
import com.sub.academy.entity.Student;
import com.sub.academy.repository.CourseRepository;
import com.sub.academy.repository.StudentRepository;
import com.sub.academy.rest.dto.request.StudentRequestDto;
import com.sub.academy.rest.exception.StudentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService{

    private final StudentRepository repository;

    private final CourseRepository courseRepository;

    @Override
    public Student read(UUID id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Override
    @Transactional
    public Student update(UUID id, StudentRequestDto dto) {
        Student existing = repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        existing.setName(dto.name());
        existing.setGroup(dto.group());
        existing.setAge(dto.age());

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
                .ifPresent(student -> {
                    student.deleteCourses();
                    repository.delete(student);
                });
    }

    @Override
    public List<Student> findByCourseId(UUID courseId) {
        return repository.findAllByCoursesId(courseId);
    }

    @Override
    public List<Student> findByGroup(String group) {
        return repository.findAllByGroup(group);
    }

    @Override
    public List<Student> findByAgeGreaterThanAndCourseId(Integer age, UUID courseId) {
        return repository.findAllByAgeGreaterThanAndCoursesId(age, courseId);
    }

    @Override
    public List<Student> findStudentsByCourseIdAndGroup(UUID courseId, String group) {
        return repository.findAllByGroupAndCoursesId(group, courseId);
    }

    @Override
    public Student create(StudentRequestDto dto) {
        Student student = toDomain(dto);
        return repository.save(student);
    }

    @Override
    public long count() {
        return repository.count();
    }

    private Student toDomain(StudentRequestDto dto) {
        Student student = new Student(dto.name(), dto.age(), dto.group());
        if (dto.courses() != null) {
            courseRepository.findAllById(dto.courses()).forEach(student::addCourse);
        }
        return student;
    }
}
