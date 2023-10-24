package com.example.springsecr.services;

import com.example.springsecr.dto.model.DepartmentCreateRequestDTO;
import com.example.springsecr.exceptions.BadRequestException;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.UserRepositories;
import com.example.springsecr.validators.DepartmentCreateDtoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentService
{
    private final DepartmentRepositories departmentRepositories;
    private final UserRepositories userRepositories;

    private final DepartmentCreateDtoValidator departmentCreateDtoValidator;
    public Department create(DepartmentCreateRequestDTO departmentDto)
    {
        BindingResult bindingResult = new DirectFieldBindingResult(departmentDto, "departmentDto");
        departmentCreateDtoValidator.validate(departmentDto, bindingResult);
        if(bindingResult.hasErrors())
        {
            throw new BadRequestException(bindingResult.getAllErrors().stream().map((err) -> err.getDefaultMessage()).collect(Collectors.joining()));
        }
        Department department = new Department();
        department.setName(departmentDto.getName());
        if(departmentDto.getDepartmentParentId() != null)
        {
            department.setDepartmentParent(departmentRepositories.findById(departmentDto.getDepartmentParentId()).get());
        }
        if(departmentDto.getBossId() != null)
        {
            User boss = userRepositories.findById(departmentDto.getBossId()).get();
            department.setBoss(boss);
            department.setModerator(boss);
        }
        if(departmentDto.getModeratorId() != null)
        {
            department.setModerator(userRepositories.findById(departmentDto.getModeratorId()).get());
        }
        //department.set
        return departmentRepositories.save(department);
    }

    public Collection<Department> getAll()
    {
        return departmentRepositories.findAll();
    }
}
