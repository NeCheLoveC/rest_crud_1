package com.example.springsecr.validators;

import com.example.springsecr.models.Role;
import com.example.springsecr.services.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class RoleValidators implements Validator
{
    private RoleService roleService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Role.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Role role = (Role) target;
        if(roleService.getRoleByName(role.getName()).isPresent())
            errors.rejectValue("name","","Уже существует роль с данным названием :" +  role.getName());
    }
}
