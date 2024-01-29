package com.example.springsecr.dto.model.request.department;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentUpdateRequestDto
{
    @JsonIgnore
    private Long id;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(min = 5)
    private String name;
}
