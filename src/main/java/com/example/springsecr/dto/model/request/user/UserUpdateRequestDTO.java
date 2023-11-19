package com.example.springsecr.dto.model.request.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateRequestDTO
{
    @JsonIgnore
    public long id;
    @Email
    @NotEmpty
    private String email;
    @NotNull
    @Size(min = 5)
    private String password;
}
