package com.example.springsecr.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateDTO
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
