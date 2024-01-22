package com.example.springsecr.services;

import com.example.springsecr.dto.converter.UserRegisterRequestConverter;
import com.example.springsecr.dto.model.request.user.UserRegisterCredentialsRequestDto;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.UserRepositories;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import com.example.springsecr.validators.UserRegistrationDtoValidator;
import com.example.springsecr.validators.UserUpdateDtoValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@Transactional
@Rollback
class UserServiceTest {
    private UserService userServiceUnderTest;
    @Mock
    private DepartmentRepositories departmentRepositories;
    @Mock
    private UserRepositories userRepo;
    @Mock
    private UserRegisterRequestConverter userRegisterConverter;
    @Mock
    private UserRegistrationDtoValidator userRegistrationDtoValidator;
    @Mock
    private UserUpdateDtoValidator userUpdateDtoValidator;
    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    private void init()
    {
        userServiceUnderTest = new UserService(departmentRepositories, userRepo,userRegisterConverter,userRegistrationDtoValidator,userUpdateDtoValidator, entityManager);
    }


    @Test
    void loadUserByUsername()
    {
    }

    @Test
    void canSaveUser()
    {
        UserRegisterCredentialsRequestDto userDto = UserRegisterCredentialsRequestDto.builder()
                .username("   anton   ")
                .password("   123456  ")
                .email("test02@mail.ru")
                .position("Java Backend Developer")
                .departmentId(1L).build();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        User user = userServiceUnderTest.saveUser(userDto);

        /*
        assertThat(user)
                .matches(u -> userDto.getUsername().equals("anton"))
                .matches(u -> userDto.getPassword())

         */

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