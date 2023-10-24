package com.example.springsecr.dto.model;

import com.example.springsecr.models.Department;
import com.example.springsecr.models.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class DepartmentCreateRequestDTO
{
    @NotBlank
    @Size(min = 5)
    private String name;

    private Long moderatorId;
    private Long bossId;
    private Long departmentParentId;

}
