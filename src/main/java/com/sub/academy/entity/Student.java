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
@Table(name = "students")
@Accessors(chain = true)
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "student_group", nullable = false)
    private String group;

    @ManyToMany(mappedBy = "students")
    private Set<Course> courses = new HashSet<>();

    public void addCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.getStudents().remove(this);
    }

    public void deleteCourses() {
        courses.forEach(c -> c.getStudents().remove(this));
        courses = new HashSet<>();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Student other = (Student) o;
        return Objects.equals(name, other.name)
                && Objects.equals(group, other.group)
                && Objects.equals(age, other.age);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, group, age);
    }

    public Student(String name, Integer age, String group) {
        this.name = name;
        this.age = age;
        this.group = group;
    }
}
