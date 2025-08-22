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
@Table(name = "teachers")
@Accessors(chain = true)
@NoArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "teacher_group", nullable = false)
    private String group;

    @ManyToMany(mappedBy = "teachers")
    private Set<Course> courses = new HashSet<>();

    public void addCourse(Course course) {
        courses.add(course);
        course.getTeachers().add(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.getTeachers().remove(this);
    }

    public void deleteCourses() {
        courses.forEach(c -> c.getTeachers().remove(this));
        courses = new HashSet<>();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Teacher other = (Teacher) o;
        return Objects.equals(name, other.name) && Objects.equals(group, other.group);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, group);
    }

    public Teacher(String name, String group) {
        this.name = name;
        this.group = group;
    }
}
