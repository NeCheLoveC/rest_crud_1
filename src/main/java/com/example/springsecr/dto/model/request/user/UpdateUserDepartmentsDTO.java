package com.example.springsecr.dto.model.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDepartmentsDTO
{
    @NotNull
    private Long departmentId;
}
