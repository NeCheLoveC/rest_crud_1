package com.example.springsecr.dto.converter;

import com.example.springsecr.dto.model.response.DepartmentResponseDto;
import com.example.springsecr.models.Department;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class DepartmentToDepartmentDtoConverter implements Function<Department, DepartmentResponseDto>
{
    @Override
    public DepartmentResponseDto apply(Department department) {
        DepartmentResponseDto departmentDto = new DepartmentResponseDto();
        departmentDto.setId(department.getId());
        departmentDto.setName(department.getName());
        Optional.ofNullable(department.getDepartmentParent()).ifPresent(p -> departmentDto.setDepartmentParentId(p.getId()));
        Optional.ofNullable(department.getBoss()).ifPresent(b -> departmentDto.setBossId(b.getId()));
        Optional.ofNullable(department.getModerator()).ifPresent(m -> departmentDto.setModeratorId(m.getId()));
        return departmentDto;
    }
}
