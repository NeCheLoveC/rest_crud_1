package com.example.springsecr.dto.model.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponseDTO
{
    private Long id;
    private String username;
    private String email;
    private DepartmentResponseDto department;
    private String position;
}
