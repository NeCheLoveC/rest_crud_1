package com.example.springsecr.dto.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentResponseDto
{
    private Long id;
    private String name;
    private Long departmentParentId;
    private Long moderatorId;
    private Long bossId;
}
