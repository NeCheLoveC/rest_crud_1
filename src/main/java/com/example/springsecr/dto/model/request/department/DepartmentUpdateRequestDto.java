package com.example.springsecr.dto.model.request.department;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentUpdateRequestDto
{
    @JsonIgnore
    private Long id;
    @NotBlank
    private String name;
    private boolean isDeleted;
}
