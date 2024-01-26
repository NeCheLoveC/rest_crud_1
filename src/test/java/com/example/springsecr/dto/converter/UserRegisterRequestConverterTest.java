package com.example.springsecr.dto.converter;

import com.example.springsecr.dto.model.request.user.UserRegisterCredentialsRequestDto;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.Role;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.models.User;
import com.example.springsecr.services.DepartmentService;
import com.example.springsecr.services.RoleService;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserRegisterRequestConverterTest
{
    BCryptEncoderWrapper encoderWrapper;
    UserRegisterRequestConverter underTest;
    @Mock
    private RoleService roleService;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private Role userRole;

    @BeforeEach
    public void init()
    {
        encoderWrapper = new BCryptEncoderWrapper();
        underTest = new UserRegisterRequestConverter();
        underTest.setDepartmentService(departmentService);
        underTest.setbCryptEncoderWrapper(encoderWrapper);
    }

    @Test
    void tryConvertDtoToUser()
    {
        try(MockedStatic<RoleService> util = Mockito.mockStatic(RoleService.class))
        {
            BCryptPasswordEncoder encoder = encoderWrapper.getbCryptEncoderWrapper();
            //GIVEN
            UserRegisterCredentialsRequestDto userDto = UserRegisterCredentialsRequestDto.builder()
                    .username(" anton  ")
                    .password("123456")
                    .email("  tEsT02@MAIL.ru  ")
                    .position("   Java Backend Developer  ")
                    .departmentId(1L)
                    .build();
            util.when(() -> RoleService.getUSER_ROLE()).thenReturn(userRole);
            doReturn(Optional.of(mock(Department.class))).when(departmentService).find(anyLong());
            //WHEN
            User result = underTest.apply(userDto);

            assertThat(result.getUsername()).isEqualTo("anton");
            assertThat(result.getEmail()).isEqualTo("test02@mail.ru");
            assertThat(result.getPosition()).isEqualTo("Java Backend Developer");
            assertThat(encoder.encode(userDto.getPassword()).equals(result.getPassword()));
        }
    }
}