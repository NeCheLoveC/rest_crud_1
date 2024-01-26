package com.example.springsecr.dto.model.request.department;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DepartmentCreateRequestDTO
{
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Pattern(regexp = ".{5,}")
    private String name;
    private Long moderatorId;
    private Long bossId;
    @NotNull(message = "Департамент должен иметь департамент-родителя")
    private Long departmentParentId;

}
