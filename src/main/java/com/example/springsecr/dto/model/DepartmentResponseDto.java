package com.example.springsecr.dto.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentResponseDto
{
    private String name;
    private Long departmentParentId;
    private Long moderatorId;
    private Long bossId;
}
