package com.example.springsecr.validators;

import com.example.springsecr.dto.model.UserUpdateDTO;
import com.example.springsecr.exceptions.BadRequestException;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.UserRepositories;
import com.example.springsecr.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

@Component
public class UserUpdateDtoValidator implements Validator
{
    private UserRepositories userRepositories;
    private DepartmentRepositories departmentRepositories;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserUpdateDTO.class);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void validate(Object target, Errors errors) throws EntityNotFoundException{
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) target;
        Optional<User> userWrapper = userRepositories.findById(userUpdateDTO.getId());

        //Если пользователь не найден по id -> ошибка
        userWrapper.orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id '%d' не найден.", userUpdateDTO.getId())));
        validateEmailAndDepartmentIntoUserUpdateDto(userUpdateDTO, userWrapper.get(), errors);
    }

    private void validateEmailAndDepartmentIntoUserUpdateDto(UserUpdateDTO userUpdateDTO, User userForUpdate, Errors errors)
    {
        //Проверка на существование User с данным id
        Optional<User> user = userRepositories.findById(userUpdateDTO.getId());
        if(user.isEmpty())
            errors.rejectValue("id", "","Пользователь");

        //Проверка email
        Optional<User> userWithSameEmail = userRepositories.getUserByEmail(userUpdateDTO.getEmail());

        if(userWithSameEmail.isPresent())
        {
            //Если найденный email уже занят другим пользователем - ошибка
            if(!Objects.equals(userWithSameEmail.get().getId(), userForUpdate.getId()))
                errors.rejectValue("email", "", String.format("Данный email (%s) уже используется другим пользователем.",userForUpdate.getEmail()));
                //throw new BadRequestException(String.format("Данный email (%s) уже используется другим пользователем.",userForUpdate.getEmail()));
        }
    }

    @Autowired
    public void setUserRepositories(UserRepositories userRepositories) {
        this.userRepositories = userRepositories;
    }

    @Autowired
    public void setDepartmentRepositories(DepartmentRepositories departmentRepositories) {
        this.departmentRepositories = departmentRepositories;
    }
}
