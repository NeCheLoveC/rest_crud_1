package com.example.springsecr.dto.model.request.department;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentUpdateRequestDto
{
    @JsonIgnore
    private Long id;
    private String name;
}
