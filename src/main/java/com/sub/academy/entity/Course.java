package com.sub.academy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity(name = "courses")
@Getter
@Setter
public class Course extends BaseEntity {

    @Enumerated(EnumType.ORDINAL)
    private CourseType type;

    @ManyToMany
    private Set<Student> students;

    @ManyToMany
    private Set<Teacher> teachers;

}
