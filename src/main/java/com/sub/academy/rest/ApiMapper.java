package com.sub.academy.rest;

import com.sub.academy.entity.Course;
import com.sub.academy.entity.Student;
import com.sub.academy.entity.Teacher;
import com.sub.academy.rest.dto.request.TeacherRequestDto;
import com.sub.academy.rest.dto.response.CourseResponseDto;
import com.sub.academy.rest.dto.response.StudentResponseDto;
import com.sub.academy.rest.dto.response.TeacherResponseDto;

import java.util.List;

import static java.util.stream.Collectors.toSet;

public class ApiMapper {

    public static Teacher toDomain(TeacherRequestDto dto) {
        return new Teacher(dto.name(), dto.group());
    }

    public static TeacherResponseDto toDto(Teacher teacher) {
        return new TeacherResponseDto(
                teacher.getId(),
                teacher.getName(),
                teacher.getGroup(),
                teacher.getCourses()
                        .stream()
                        .map(Course::getId)
                        .collect(toSet()));
    }

    public static CourseResponseDto toDto(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getName(),
                course.getType(),

                course.getStudents()
                        .stream()
                        .map(Student::getId)
                        .collect(toSet()),

                course.getTeachers()
                        .stream()
                        .map(Teacher::getId)
                        .collect(toSet()));
    }

    public static StudentResponseDto toDto(Student student) {
        return new StudentResponseDto(
                student.getId(),
                student.getName(),
                student.getAge(),
                student.getGroup(),
                student.getCourses()
                        .stream()
                        .map(Course::getId)
                        .collect(toSet()));
    }

    public static List<StudentResponseDto> toDto(List<Student> students) {
        return students.stream().map(ApiMapper::toDto).toList();
    }

    public static List<TeacherResponseDto> toDtoList(List<Teacher> teachers) {
        return teachers.stream().map(ApiMapper::toDto).toList();
    }
}