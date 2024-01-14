package com.example.springsecr.dto.converter;

import com.example.springsecr.dto.model.request.user.UserRegisterCredentialsRequestDto;
import com.example.springsecr.exceptions.BadRequestException;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.Role;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.models.User;
import com.example.springsecr.services.DepartmentService;
import com.example.springsecr.services.RoleService;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class UserRegisterRequestConverter implements Function<UserRegisterCredentialsRequestDto, User>
{
    private BCryptEncoderWrapper bCryptEncoderWrapper;
    private RoleService roleService;
    private DepartmentService departmentService;

    @Override
    public User apply(UserRegisterCredentialsRequestDto object) {
        //RoleType userRole = RoleType.valueOf(object.getRole().substring(RoleType.ROLE_SUFFIX.length()));
        Role usersAuthorities = RoleService.getUSER_ROLE();
        Optional<Department> department = departmentService.find(object.getDepartmentId());
        department.orElseThrow(() -> new BadRequestException(String.format("Департамент с id = %d не найден", object.getDepartmentId())));
        User user = new User(object.getUsername(),bCryptEncoderWrapper.getbCryptEncoderWrapper().encode(object.getPassword()), object.getEmail(), usersAuthorities, department.get());
        return user;
    }

    @Autowired
    public void setbCryptEncoderWrapper(@Lazy BCryptEncoderWrapper bCryptEncoderWrapper) {
        this.bCryptEncoderWrapper = bCryptEncoderWrapper;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setDepartmentService(@Lazy DepartmentService departmentService) {
        this.departmentService = departmentService;
    }
}
