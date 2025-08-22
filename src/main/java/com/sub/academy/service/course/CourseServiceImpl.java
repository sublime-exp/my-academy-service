package com.sub.academy.service;

import com.sub.academy.entity.Course;
import com.sub.academy.entity.CourseType;
import com.sub.academy.entity.Student;
import com.sub.academy.entity.Teacher;
import com.sub.academy.repository.CourseRepository;
import com.sub.academy.repository.StudentRepository;
import com.sub.academy.repository.TeacherRepository;
import com.sub.academy.rest.dto.request.CoursePatchDto;
import com.sub.academy.rest.dto.request.CourseRequestDto;
import com.sub.academy.rest.dto.request.StudentRequestDto;
import com.sub.academy.rest.dto.request.TeacherRequestDto;
import com.sub.academy.rest.exception.CourseNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    public Course create(CourseRequestDto dto) {
        Course course = toDomain(dto);
        return courseRepository.saveAndFlush(course);
    }

    @Override
    public Course read(UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
    }

    @Override
    @Transactional
    public Course update(UUID id, CourseRequestDto course) {
        Course existing = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
        Course updated = toDomain(course);
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setStudents(updated.getStudents());
        existing.setTeachers(updated.getTeachers());
        return existing;
    }

    @Override
    @Transactional
    public Course patch(UUID id, CoursePatchDto patch) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));

        Optional.ofNullable(patch.name()).ifPresent(course::setName);
        Optional.ofNullable(patch.type()).ifPresent(course::setType);

        Optional.ofNullable(patch.students())
                .ifPresent(students -> {
                    course.deleteStudents();
                    students.forEach(sid -> course.addStudent(studentRepository.getReferenceById(sid)));
                });

        Optional.ofNullable(patch.newStudents())
                .ifPresent(newStudents -> {
                    course.deleteStudents();
                    newStudents.stream().map(this::toDomain).forEach(course::addStudent);
                });


        Optional.ofNullable(patch.teachers())
                .ifPresent(teachers -> {
                    course.deleteTeachers();
                    teachers.forEach(tid -> course.addTeacher(teacherRepository.getReferenceById(tid)));
                });

        Optional.ofNullable(patch.newTeachers())
                .ifPresent(newTeachers -> {
                    course.deleteTeachers();
                    newTeachers.stream().map(this::toDomain).forEach(course::addTeacher);
                });

        return course;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        courseRepository
                .findById(id)
                .ifPresent(course -> {
                    course.deleteTeachers();
                    course.deleteStudents();
                    courseRepository.delete(course);
                });
    }

    @Override
    public long countByType(CourseType type) {
        return courseRepository.countByType(type);
    }

    private Course toDomain(CourseRequestDto dto) {
        Course course = new Course(dto.name(), dto.type());
        if (dto.students() != null) {
            studentRepository.findAllById(dto.students()).forEach(course::addStudent);
        }

        if (dto.newStudents() != null) {
            Set<Student> students = dto.newStudents().stream().map(this::toDomain).collect(Collectors.toSet());
            course.setStudents(students);
        }

        if (dto.teachers() != null) {
            teacherRepository.findAllById(dto.teachers()).forEach(course::addTeacher);
        }

        if (dto.newTeachers() != null) {
            Set<Teacher> teachers = dto.newTeachers().stream().map(this::toDomain).collect(Collectors.toSet());
            course.setTeachers(teachers);
        }

        return course;
    }

    private Student toDomain(StudentRequestDto dto) {
        Student student = new Student(dto.name(), dto.age(), dto.group());
        if (dto.courses() != null) {
            courseRepository.findAllById(dto.courses()).forEach(student::addCourse);
        }
        return student;
    }

    private Teacher toDomain(TeacherRequestDto dto) {
        Teacher teacher = new Teacher(dto.name(), dto.group());
        if (dto.courses() != null) {
            courseRepository.findAllById(dto.courses()).forEach(teacher::addCourse);
        }
        return teacher;
    }
}
