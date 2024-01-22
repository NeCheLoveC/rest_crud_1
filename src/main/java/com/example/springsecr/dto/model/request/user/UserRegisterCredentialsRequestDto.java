package com.example.springsecr.dto.model.request.user;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRegisterCredentialsRequestDto
{
    @NotNull(message = "username is NOT NULL")
    @Size(min = 5, message = "username должен быть минимум из 5 символов")
    private String username;
    @NotNull(message = "message is NOT NULL")
    @Size(min = 5, message = "Минимальная длина пароля - 5 символов")
    private String password;
    @NotEmpty
    @Email
    private String email;
    @NotNull(message = "Департамент не не должен быть null")
    private Long departmentId;
    @NotBlank
    private String position;
}
