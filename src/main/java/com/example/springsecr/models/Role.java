package com.example.springsecr.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "role")
public class Role {
    private static final String generatorName = "role_sequence_gen";

    @Id
    @SequenceGenerator(name = generatorName, sequenceName = generatorName)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    protected Role(){}

    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Role object = (Role) o;
        return this.name.equals(object.getName());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
