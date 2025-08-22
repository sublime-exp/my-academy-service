package com.sub.academy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "courses")
@Accessors(chain = true)
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CourseType type;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "course_student",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "course_teacher",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Teacher> teachers = new HashSet<>();

    public void addStudent(Student student) {
        if (students.add(student)) {
            student.getCourses().add(this);
        }
    }

    public void deleteStudents() {
        students.forEach(s -> s.getCourses().remove(this));
        students = new HashSet<>();
    }

    public void addTeacher(Teacher teacher) {
        if (teachers.add(teacher)) {
            teacher.getCourses().add(this);
        }
    }

    public void deleteTeachers() {
        teachers.forEach(t -> t.getCourses().remove(this));
        teachers = new HashSet<>();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Course other = (Course) o;
        return Objects.equals(name, other.name) && Objects.equals(type, other.type);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, type);
    }

    public Course(String name, CourseType type) {
        this.name = name;
        this.type = type;
    }
}