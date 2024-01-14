package com.example.springsecr.validators;

import com.example.springsecr.dto.model.request.user.UserUpdateRequestDTO;
import com.example.springsecr.exceptions.HttpCustomException;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.UserRepositories;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
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
        return clazz.equals(UserUpdateRequestDTO.class);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void validate(Object target, Errors errors) throws EntityNotFoundException{
        UserUpdateRequestDTO userUpdateDTO = (UserUpdateRequestDTO) target;
        Optional<User> userWrapper = userRepositories.findById(userUpdateDTO.getId());

        //Если пользователь не найден по id -> ошибка
        userWrapper.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND, String.format("Пользователь с id '%d' не найден.", userUpdateDTO.getId())));
        validateEmailIntoUserUpdateDto(userUpdateDTO, errors);
    }

    private void validateEmailIntoUserUpdateDto(UserUpdateRequestDTO userUpdateDTO, Errors errors)
    {
        User user = userRepositories.findById(userUpdateDTO.getId()).get();
        //Проверка email
        Optional<User> userWithSameEmail = userRepositories.getUserByEmail(userUpdateDTO.getEmail());

        //Если найденный email уже занят другим пользователем - ошибка

        if(userWithSameEmail.isPresent() && !userWithSameEmail.get().equals(user))
        {
            //Если найденный email уже занят другим пользователем - ошибка
            errors.rejectValue("email", "", String.format("Данный email (%s) уже используется другим пользователем с id = %d",userUpdateDTO.getEmail(), userWithSameEmail.get().getId()));
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
