package com.example.springsecr.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_username_unq", columnList = "username", unique = true)
})
public class User {
    private static final String generatorName = "users_sequence_gen";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = generatorName)
    @SequenceGenerator(name = generatorName, sequenceName = generatorName)
    private Long id;
    @NotNull
    @Size(min = 5)
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotNull
    @Size(min = 5)
    @Column(name = "password", nullable = false)
    private String password;

    @Email
    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @NotEmpty
    @ManyToMany
    @JoinTable(
            name = "userr_rolee",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false)
    )
    private Collection<Role> roles = new ArrayList<>();

    protected User(){}

    private User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private User(String username, String password, Collection<Role> roles)
    {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public User(String username, String password, String email, Collection<Role> roles) {
        this(username,password,roles);
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
