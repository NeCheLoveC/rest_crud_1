package com.example.springsecr.dto.model;

import com.example.springsecr.models.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponseDTO
{
    private String username;
    private String email;
    private DepartmentResponseDto department;
}
