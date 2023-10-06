package com.example.springsecr.dto.model;

import com.example.springsecr.models.RoleType;
import com.example.springsecr.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Collections;

public class UserRegisterCredentionalsDto
{
    @NotNull(message = "username is NOT NULL")
    @Size(min = 5, message = "username должен быть минимум 5 символов")
    private String username;
    @NotNull(message = "message is NOT NULL")
    @Size(min = 5, message = "Минимальная длина пароля - 5 символов")
    private String password;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Pattern(regexp = "(ROLE_USER)|(ROLE_MODERATOR)")
    private String role;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
