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
    @Email(message = "email должен иметь формат электронного адреса")
    @NotNull
    private String email;
    @NotNull
    private String position;
}
