package com.example.springsecr.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "department")
@Setter
@Getter
public class Department
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_generator_id_seq")
    @SequenceGenerator(name = "department_generator_id_seq", sequenceName = "department_generator_id_seq")
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    private String name;
    @OneToOne
    @JoinTable(
            name = "department_user",
            joinColumns = {@JoinColumn(name = "department_id", unique = true, nullable = false),},
            inverseJoinColumns = {@JoinColumn(name = "user_id", unique = true, nullable = false)}
    )
    private User moderator;
    @OneToOne
    @JoinTable(
            name = "department_boss",
            joinColumns = {@JoinColumn(name = "department_id", unique = true,nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "boss_id", unique = true,nullable = false)}
    )
    private User boss;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_parent_id")
    private Department departmentParent;

    @OneToMany(mappedBy = "departmentParent")
    private Collection<Department> departments;
}
