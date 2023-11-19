package com.example.springsecr.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Entity
@Table(name = "department", indexes = {
        @Index(name = "idx_department_department_parent_id", columnList = "department_parent_id"),
        @Index(name = "idx_department_name", columnList = "name")
})
@Setter
@Getter
public class Department
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_generator_id_seq")
    @SequenceGenerator(name = "department_generator_id_seq", sequenceName = "department_generator_id_seq")
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    @OneToOne
    @JoinTable(
            name = "department_user",
            joinColumns = {@JoinColumn(name = "department_id", unique = true, nullable = false),},
            inverseJoinColumns = {@JoinColumn(name = "user_id", unique = true, nullable = false)}
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User moderator;
    @OneToOne
    @JoinTable(
            name = "department_boss",
            joinColumns = {@JoinColumn(name = "department_id", unique = true,nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "boss_id", unique = true,nullable = false)}
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User boss;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_parent_id")
    private Department departmentParent;

    @OneToMany(mappedBy = "departmentParent")
    private Collection<Department> departments;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public Department() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Department that = (Department) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
