package com.example.springsecr.validators;

import com.example.springsecr.dto.model.request.user.UserRegisterCredentialsRequestDto;
import com.example.springsecr.services.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserRegistrationDtoValidator implements Validator
{
    private UserService userService;

    public UserRegistrationDtoValidator(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserRegisterCredentialsRequestDto.class);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void validate(Object target, Errors errors)
    {
        UserRegisterCredentialsRequestDto userCredentionalsDto = (UserRegisterCredentialsRequestDto) target;
        validateUsername(userCredentionalsDto.getUsername(), errors);
        validateEmail(userCredentionalsDto.getEmail(), errors);
    }

    private void validateUsername(String username, Errors errors)
    {
        if(userService.getUserByUsername(username).isPresent())
        {
            errors.rejectValue("username", "","Нарушение ограничения уникальности");
        }
    }

    private void validateEmail(String email, Errors errors)
    {
        if(userService.getUserByEmail(email).isPresent())
        {
            errors.rejectValue("email", "","Нарушение ограничения уникальности");
        }
    }
}
