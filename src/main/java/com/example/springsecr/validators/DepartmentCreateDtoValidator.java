package com.example.springsecr.validators;

import com.example.springsecr.dto.model.request.department.DepartmentCreateRequestDTO;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class DepartmentCreateDtoValidator implements Validator
{
    private DepartmentRepositories departmentRepositories;
    private UserRepositories userRepositories;
    @Override
    public boolean supports(Class<?> clazz)
    {
        return clazz.equals(DepartmentCreateRequestDTO.class);
    }
    @Transactional()
    @Override
    public void validate(Object target, Errors errors)
    {
        DepartmentCreateRequestDTO departmentDto = (DepartmentCreateRequestDTO) target;

        if(departmentRepositories.existByDepartmentByName(departmentDto.getName()))
            errors.rejectValue("name", "", "Имя департамента уже занято");
        if(departmentDto.getDepartmentParentId() != null)
        {
            Optional<Department> parentDepartment = departmentRepositories.findById(departmentDto.getDepartmentParentId());
            if(parentDepartment.isEmpty())
                errors.rejectValue("departmentParentId", "", String.format("Департамент с id = %d не найден", departmentDto.getDepartmentParentId()));
        }
        if(departmentDto.getBossId() != null)
        {
            Optional<User> boss = userRepositories.findById(departmentDto.getBossId());
            if(boss.isEmpty())
                errors.rejectValue("bossId", "",String.format("Пользователь с id = %d не найден.", departmentDto.getBossId()));
        }
        if(departmentDto.getModeratorId() != null)
        {
            Optional<User> moderator = userRepositories.findById(departmentDto.getModeratorId());
            if(moderator.isEmpty())
                errors.rejectValue("moderatorId", "",String.format("Пользователь с id = %d не найден", departmentDto.getModeratorId()));
        }
    }

    @Autowired
    public void setDepartmentRepositories(DepartmentRepositories departmentRepositories) {
        this.departmentRepositories = departmentRepositories;
    }

    @Autowired
    public void setUserRepositories(UserRepositories userRepositories) {
        this.userRepositories = userRepositories;
    }
}
