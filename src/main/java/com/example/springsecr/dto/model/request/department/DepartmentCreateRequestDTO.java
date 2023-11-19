package com.example.springsecr.dto.model.request.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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
