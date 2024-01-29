package com.example.springsecr.validators;

import com.example.springsecr.dto.model.request.user.UserUpdateRequestDTO;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.UserRepositories;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class UserUpdateDtoValidatorTest {
    UserUpdateDtoValidator underTest;
    @Mock
    private UserRepositories userRepositories;
    @BeforeEach
    private void init()
    {
        underTest = new UserUpdateDtoValidator();
        underTest.setUserRepositories(userRepositories);
    }
    @Test
    void validateEmailIntoUserUpdateDto_ifEmailAlreadyUseAnotherUser()
    {
        //GIVEN
        User user = mock();
        User userWithSameEmail = mock();
        UserUpdateRequestDTO userUpdateDTO = mock();
        doReturn(1L).when(userUpdateDTO).getId();
        doReturn(2L).when(userWithSameEmail).getId();
        //doReturn("test@mail.ru").when(userWithSameEmail).getEmail();
        doReturn("test@mail.ru").when(userUpdateDTO).getEmail();

        doReturn(Optional.of(user)).when(userRepositories).findById(any());
        doReturn(Optional.of(userWithSameEmail)).when(userRepositories).getUserByEmail(any());

        Errors errors = mock();
        //WHEN
        underTest.validate(userUpdateDTO, errors);

        //THEN
        verify(errors, times(1)).rejectValue("email","",String.format("Данный email (%s) уже используется другим пользователем с id = %d",userUpdateDTO.getEmail(), userWithSameEmail.getId()));
    }
}