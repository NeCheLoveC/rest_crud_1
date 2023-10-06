package com.example.springsecr.dto.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsernamePasswordDTO
{
    @NotNull(message = "username is NOT NULL")
    @Size(min = 5, message = "username должен быть минимум 5 символов")
    private String username;
    @NotNull(message = "message is NOT NULL")
    @Size(min = 5, message = "Минимальная длина пароля - 5 символов")
    private String password;
}
