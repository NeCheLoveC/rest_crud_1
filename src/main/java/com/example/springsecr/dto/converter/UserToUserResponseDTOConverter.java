package com.example.springsecr.dto.converter;

import com.example.springsecr.dto.model.response.UserResponseDTO;
import com.example.springsecr.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserToUserResponseDTOConverter implements Function<User, UserResponseDTO>
{
    DepartmentToDepartmentResponseDtoConverter departmentDtoConverter;
    @Override
    public UserResponseDTO apply(User user) {
        UserResponseDTO userDto = new UserResponseDTO();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setPosition(user.getPosition());
        if(user.getDepartment() != null)
            userDto.setDepartment(departmentDtoConverter.apply(user.getDepartment()));
        return userDto;
    }

    @Autowired
    public void setDepartmentDtoConverter(DepartmentToDepartmentResponseDtoConverter departmentDtoConverter) {
        this.departmentDtoConverter = departmentDtoConverter;
    }
}
