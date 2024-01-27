package com.example.springsecr.services;

import com.example.springsecr.dto.converter.UserRegisterRequestConverter;
import com.example.springsecr.dto.model.request.user.UserRegisterCredentialsRequestDto;
import com.example.springsecr.exceptions.HttpCustomException;
import com.example.springsecr.models.Role;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.UserRepositories;
import com.example.springsecr.validators.UserRegistrationDtoValidator;
import com.example.springsecr.validators.UserUpdateDtoValidator;
import jakarta.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserService userServiceUnderTest;
    @Mock
    private DepartmentRepositories departmentRepositories;
    @Mock
    private UserRepositories userRepositories;
    @Mock
    private UserRegisterRequestConverter userRegisterConverter;
    @Mock
    private UserRegistrationDtoValidator userRegistrationDtoValidator;
    @Mock
    private UserUpdateDtoValidator userUpdateDtoValidator;
    @Mock
    private EntityManager entityManager;

    @BeforeEach
    private void init()
    {
        userServiceUnderTest = new UserService(departmentRepositories, userRepositories,userRegisterConverter,userRegistrationDtoValidator,userUpdateDtoValidator, entityManager);
    }


    @Test
    void loadUserByUsername()
    {
        final String USERNAME = "admin";
        final String PASSWORD = "123456";
        User admin = mock();
        Role role = mock();
        doReturn(RoleType.USER.getRoleName()).when(role).getName();
        doReturn(USERNAME).when(admin).getUsername();
        doReturn(PASSWORD).when(admin).getPassword();
        doReturn(role).when(admin).getRole();
        doReturn(Optional.of(admin)).when(userRepositories).getUserByUsername(USERNAME);

        userServiceUnderTest.loadUserByUsername(USERNAME);

        verify(userRepositories,times(1)).getUserByUsername(USERNAME);
    }

    @Test
    void canSaveUser()
    {
        //GIVEN
        UserRegisterCredentialsRequestDto userDto = UserRegisterCredentialsRequestDto.builder()
                .username("anton")
                .password("123456")
                .email("test02@mail.ru")
                .position("Java Backend Developer")
                .departmentId(1L)
                .build();

        //WHEN
        User user = userServiceUnderTest.saveUser(userDto);

        //THEN
        verify(userRegistrationDtoValidator, times(1)).validate(any(),any());
        verify(userRepositories, times(1)).save(any());
    }

    @Test
    void trySaveUser_WhenExistUserWithSameEmail()
    {
        //GIVEN
        UserRegisterCredentialsRequestDto userDto = UserRegisterCredentialsRequestDto.builder()
                .username("anton")
                .password("123456")
                .email("test02@mail.ru")
                .position("Java Backend Developer")
                .departmentId(1L)
                .build();

        User userWithSameEmail = mock();

        Mockito.doAnswer(invocationOnMock -> {
            Errors errors = invocationOnMock.getArgument(1);
            errors.rejectValue("email","", "Email занят");
            return null;
        }).when(userRegistrationDtoValidator).validate(any(),any());

        //WHEN
        HttpCustomException error = Assertions.catchThrowableOfType(() -> userServiceUnderTest.saveUser(userDto), HttpCustomException.class);


        //THEN
        verify(userRegistrationDtoValidator, times(1)).validate(any(),any());
        assertThat(error.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void update() {
    }

    @Test
    void setDepartment() {
    }

    @Test
    void findUsersByPredicate() {
    }

    @Test
    void findById() {
    }

    @Test
    void getUserByEmail() {
    }

    @Test
    void getCountUser() {
    }

    @Test
    void getAdmin() {
    }
}