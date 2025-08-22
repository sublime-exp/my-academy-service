package com.sub.academy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Entity(name = "students")
@Getter
@Setter
public class Student extends BaseEntity {

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String group;

    @ManyToMany
    private Set<Course> courses;
}
