package com.example.springsecr.services;

import com.example.springsecr.dto.model.request.department.DepartmentCreateRequestDTO;
import com.example.springsecr.exceptions.HttpCustomException;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.Role;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.UserRepositories;
import com.example.springsecr.validators.DepartmentCreateDtoValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DepartmentServiceTest {
    @Mock
    private Department rootDepartment;
    @Mock
    private Role userRole;
    private DepartmentService departmentServiceUnderTest;
    @Mock
    private DepartmentRepositories departmentRepositories;
    @Mock
    private  UserRepositories userRepositories;
    @Mock
    private DepartmentCreateDtoValidator departmentCreateDtoValidator;
    @BeforeEach
    private void init()
    {
        departmentServiceUnderTest = new DepartmentService(departmentRepositories,userRepositories,departmentCreateDtoValidator);
    }
    @Test
    void tryCreateDepartmentWithoutDefaultBossAndModerator()
    {
        //GIVEN
        Long parentId = 1L;
        DepartmentCreateRequestDTO createRequestDTO =
                DepartmentCreateRequestDTO.builder()
                        .departmentParentId(parentId)
                        .name("BACKEND Департамент")
                        .build();
        Department parentDepartment = Mockito.mock();
        Optional<Department> departmentWrapper = Optional.of(parentDepartment);
        Mockito.doReturn(departmentWrapper).when(departmentRepositories).findById(parentId);

        //doReturn(null).when(rootDepartment).getDepartmentParent();
        //doReturn(RoleType.USER.getRoleName()).when(role).getName();
        //WHEN
        departmentServiceUnderTest.create(createRequestDTO);


        //THEN
        verify(departmentRepositories, times(1)).findById(parentId);
        verify(departmentRepositories, times(1)).save(any());

        verify(userRepositories, never()).findByIdPessimisticLockRead(any());

        ArgumentCaptor<DepartmentCreateRequestDTO> requestDtoArg  = ArgumentCaptor.forClass(DepartmentCreateRequestDTO.class);
        verify(departmentCreateDtoValidator, times(1)).validate(requestDtoArg.capture(),any(BindingResult.class));
        DepartmentCreateRequestDTO requestCaptured = requestDtoArg.getValue();

        assertThat(createRequestDTO).isEqualTo(requestCaptured);
    }

    @Test
    void tryCreateDepartmentWithBossAndModerator()
    {
        //GIVEN
        Long parentId = 1L;
        DepartmentCreateRequestDTO createRequestDTO =
                DepartmentCreateRequestDTO.builder()
                        .departmentParentId(parentId)
                        .name("BACKEND Департамент")
                        .bossId(1L)
                        .moderatorId(2L)
                        .build();
        Department parentDepartment = Mockito.mock();
        Optional<Department> departmentWrapper = Optional.of(parentDepartment);
        doReturn(departmentWrapper).when(departmentRepositories).findById(parentId);

        User boss = Mockito.mock();
        Optional<User> bossWrapper = Optional.of(boss);
        doReturn(bossWrapper).when(userRepositories).findByIdPessimisticLockRead(1L);

        User moderator = Mockito.mock();
        Optional<User> moderatorWrapper = Optional.of(moderator);
        doReturn(moderatorWrapper).when(userRepositories).findByIdPessimisticLockRead(2L);


        //WHEN
        departmentServiceUnderTest.create(createRequestDTO);


        //THEN
        verify(departmentRepositories, times(1)).findById(parentId);
        verify(departmentRepositories, times(1)).save(any());

        verify(userRepositories, times(2)).findByIdPessimisticLockRead(any());

        ArgumentCaptor<DepartmentCreateRequestDTO> requestDtoArg  = ArgumentCaptor.forClass(DepartmentCreateRequestDTO.class);
        verify(departmentCreateDtoValidator, times(1)).validate(requestDtoArg.capture(),any(BindingResult.class));
        DepartmentCreateRequestDTO requestCaptured = requestDtoArg.getValue();

        assertThat(createRequestDTO).isEqualTo(requestCaptured);
    }

    @Test
    void tryCreateDepartmentWithBoss()
    {
        //GIVEN
        Long parentId = 1L;
        DepartmentCreateRequestDTO createRequestDTO =
                DepartmentCreateRequestDTO.builder()
                        .departmentParentId(parentId)
                        .name("BACKEND Департамент")
                        .bossId(1L)
                        .build();
        Department parentDepartment = Mockito.mock();
        Optional<Department> departmentWrapper = Optional.of(parentDepartment);
        doReturn(departmentWrapper).when(departmentRepositories).findById(parentId);

        User boss = Mockito.mock();
        Optional<User> bossWrapper = Optional.of(boss);
        doReturn(bossWrapper).when(userRepositories).findByIdPessimisticLockRead(1L);


        //WHEN
        departmentServiceUnderTest.create(createRequestDTO);


        //THEN
        verify(departmentRepositories, times(1)).findById(parentId);
        verify(departmentRepositories, times(1)).save(any());

        verify(userRepositories, times(1)).findByIdPessimisticLockRead(any());

        ArgumentCaptor<DepartmentCreateRequestDTO> requestDtoArg  = ArgumentCaptor.forClass(DepartmentCreateRequestDTO.class);
        verify(departmentCreateDtoValidator, times(1)).validate(requestDtoArg.capture(),any(BindingResult.class));
        DepartmentCreateRequestDTO requestCaptured = requestDtoArg.getValue();
        assertThat(createRequestDTO).isEqualTo(requestCaptured);
    }


    @Test
    void tryToGetAllActiveDepartments()
    {
        //WHEN
        departmentServiceUnderTest.getAllActiveDepartments();
        //THEN
        verify(departmentRepositories, only()).getAllActiveDepartments();
    }

    @Test
    void tryToSetModeratorWhenUserDoesNotExist()
    {
        //GIVING
        final Long MODERATOR_ID = 1L;
        final Long DEPARTMENT_ID = 2L;

        doReturn(Optional.empty()).when(userRepositories).findById(any());

        //WHEN
        HttpCustomException exception = catchThrowableOfType(() -> departmentServiceUnderTest.setDepartmentModerator(MODERATOR_ID,DEPARTMENT_ID), HttpCustomException.class);

        //THEN
        assertThat(exception).hasMessage(String.format("Пользователь с id = %d не найден.",MODERATOR_ID));
        assertThat(exception).isExactlyInstanceOf(HttpCustomException.class);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void tryToSetModeratorWhenDepartmentDoesNotExist()
    {
        //GIVING
        final Long MODERATOR_ID = 1L;
        final Long DEPARTMENT_ID = 2L;

        doReturn(Optional.of(Mockito.<User>mock())).when(userRepositories).findById(MODERATOR_ID);
        doReturn(Optional.empty()).when(departmentRepositories).findByIdWithPessimisticWRITE(DEPARTMENT_ID);

        //WHEN
        HttpCustomException exception = catchThrowableOfType(() -> departmentServiceUnderTest.setDepartmentModerator(MODERATOR_ID,DEPARTMENT_ID), HttpCustomException.class);


        //THEN
        verify(userRepositories, only()).findById(MODERATOR_ID);
        assertThat(exception).hasMessage(String.format("Департамент с id = %d не найден.",DEPARTMENT_ID));
        assertThat(exception).isExactlyInstanceOf(HttpCustomException.class);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void tryToSetModeratorWhenModeratorHasAdminRole() {
        //GIVING
        final Long MODERATOR_ID = 1L;
        final Long DEPARTMENT_ID = 2L;
        try (MockedStatic<RoleService> utilities = Mockito.mockStatic(RoleService.class)) {
            Department department = mock();
            User moderator = mock();
            Role adminRole = mock();

            doReturn(Optional.of(department)).when(departmentRepositories).findByIdWithPessimisticWRITE(DEPARTMENT_ID);
            doReturn(Optional.of(moderator)).when(userRepositories).findById(MODERATOR_ID);

            doReturn(adminRole).when(moderator).getRole();
            //doReturn(mock(Department.class)).when(department).getDepartmentParent();

            utilities.when(() -> RoleService.getADMIN_ROLE()).thenReturn(adminRole);

            //WHEN
            HttpCustomException err = catchThrowableOfType(() -> departmentServiceUnderTest.setDepartmentModerator(MODERATOR_ID, DEPARTMENT_ID), HttpCustomException.class);

            //THEN
            assertThat(err)
                    .isExactlyInstanceOf(HttpCustomException.class)
                    .hasMessage("Нельзя менять департамент у Администратора");
            assertThat(err.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    void tryToSetModeratorWhenDepartmentIsRootDepartment() {
        //GIVING
        final Long MODERATOR_ID = 1L;
        final Long DEPARTMENT_ID = 2L;
        try (MockedStatic<RoleService> utilities = Mockito.mockStatic(RoleService.class)){
            Department department = mock();
            User moderator = mock();
            Role adminRole = mock();

            doReturn(Optional.of(department)).when(departmentRepositories).findByIdWithPessimisticWRITE(DEPARTMENT_ID);
            doReturn(Optional.of(moderator)).when(userRepositories).findById(MODERATOR_ID);

            doReturn(userRole).when(moderator).getRole();
            doReturn(null).when(department).getDepartmentParent();

            utilities.when(() ->RoleService.getADMIN_ROLE()).thenReturn(adminRole);

            //WHEN
            HttpCustomException err = catchThrowableOfType(() -> departmentServiceUnderTest.setDepartmentModerator(MODERATOR_ID, DEPARTMENT_ID),HttpCustomException.class) ;

            //THEN
            assertThat(err)
                    .isExactlyInstanceOf(HttpCustomException.class)
                    .hasMessage("Главный департамент - неизменяемая сущность");
            assertThat(err.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    //Пользователь находится ниже по иерархии в структуре департаментов относительно целевого департамента
    //Целевой департамент - департамент, в который необходимо установить мордератора
    @Test
    void tryToSetModeratorWhenUserDoNotHaveRights() {
        //GIVING
        final Long MODERATOR_ID = 1L;
        final Long DEPARTMENT_ID = 2L;
        try (MockedStatic<RoleService> utilities = Mockito.mockStatic(RoleService.class)){
            Department department = mock();
            User moderator = mock();
            Role adminRole = mock();

            doReturn(Optional.of(department)).when(departmentRepositories).findByIdWithPessimisticWRITE(DEPARTMENT_ID);
            doReturn(Optional.of(moderator)).when(userRepositories).findById(MODERATOR_ID);

            doReturn(userRole).when(moderator).getRole();
            doReturn(mock(Department.class)).when(department).getDepartmentParent();
            doReturn(department).when(moderator).getDepartment();

            doReturn(DEPARTMENT_ID).when(department).getId();
            doReturn("egor").when(moderator).getUsername();
            doReturn("BACKEND - департамент").when(department).getName();

            doReturn(false).when(departmentRepositories).departmentIsSubDepartmentOf(anyLong(),anyLong());

            utilities.when(() ->RoleService.getADMIN_ROLE()).thenReturn(adminRole);

            //WHEN
            HttpCustomException err = catchThrowableOfType(() -> departmentServiceUnderTest.setDepartmentModerator(MODERATOR_ID, DEPARTMENT_ID),HttpCustomException.class) ;

            //THEN
            assertThat(err)
                    .isExactlyInstanceOf(HttpCustomException.class)
                    .hasMessage(String.format("Нельзя установить модератора (%s) на департамент (%s)\n " +
                            "Модератор должен быть закреплен за данным департаментом или за депертаментом выше по иерархии.", moderator.getUsername(),department.getName()));
            assertThat(err.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }




    @Test
    void tryToSetBossWhenDepartmentDoesNotExist()
    {
        //GIVING
        final Long BOSS_ID = 1L;
        final Long DEPARTMENT_ID = 2L;

        doReturn(Optional.of(Mockito.<User>mock())).when(userRepositories).findByIdPessimisticLockRead(BOSS_ID);
        doReturn(Optional.empty()).when(departmentRepositories).findByIdWithPessimisticREAD(DEPARTMENT_ID);

        //WHEN
        HttpCustomException exception = catchThrowableOfType(() -> departmentServiceUnderTest.setDepartmentBoss(BOSS_ID,DEPARTMENT_ID), HttpCustomException.class);


        //THEN
        verify(userRepositories, only()).findByIdPessimisticLockRead(BOSS_ID);
        verify(departmentRepositories, only()).findByIdWithPessimisticREAD(DEPARTMENT_ID);
        assertThat(exception).hasMessage(String.format("Департамент с id = %d не найден.",DEPARTMENT_ID));
        assertThat(exception).isExactlyInstanceOf(HttpCustomException.class);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void tryToSetBossWhenBossHasAdminRole() {
        //GIVING
        final Long BOSS_ID = 1L;
        final Long DEPARTMENT_ID = 2L;
        try (MockedStatic<RoleService> utilities = Mockito.mockStatic(RoleService.class)) {
            Department department = mock();
            User moderator = mock();
            Role adminRole = mock();

            doReturn(Optional.of(department)).when(departmentRepositories).findByIdWithPessimisticREAD(DEPARTMENT_ID);
            doReturn(Optional.of(moderator)).when(userRepositories).findByIdPessimisticLockRead(BOSS_ID);

            doReturn(adminRole).when(moderator).getRole();

            utilities.when(() -> RoleService.getADMIN_ROLE()).thenReturn(adminRole);

            //WHEN
            HttpCustomException err = catchThrowableOfType(() -> departmentServiceUnderTest.setDepartmentBoss(BOSS_ID, DEPARTMENT_ID), HttpCustomException.class);

            //THEN
            assertThat(err)
                    .isExactlyInstanceOf(HttpCustomException.class)
                    .hasMessage("Нельзя менять департамент у Администратора");
            assertThat(err.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    void tryToSetBossWhenDepartmentIsRootDepartment() {
        //GIVING
        final Long MODERATOR_ID = 1L;
        final Long DEPARTMENT_ID = 2L;
        try (MockedStatic<RoleService> utilities = Mockito.mockStatic(RoleService.class)){
            Department department = mock();
            User moderator = mock();
            Role adminRole = mock();

            doReturn(Optional.of(department)).when(departmentRepositories).findByIdWithPessimisticREAD(DEPARTMENT_ID);
            doReturn(Optional.of(moderator)).when(userRepositories).findByIdPessimisticLockRead(MODERATOR_ID);

            doReturn(userRole).when(moderator).getRole();
            doReturn(null).when(department).getDepartmentParent();

            utilities.when(() ->RoleService.getADMIN_ROLE()).thenReturn(adminRole);

            //WHEN
            HttpCustomException err = catchThrowableOfType(() -> departmentServiceUnderTest.setDepartmentBoss(MODERATOR_ID, DEPARTMENT_ID),HttpCustomException.class) ;

            //THEN
            assertThat(err)
                    .isExactlyInstanceOf(HttpCustomException.class)
                    .hasMessage("Главный департамент - неизменяемая сущность");
            assertThat(err.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }


    @Test
    void tryToSetModerator()
    {
        //GIVING
        final Long MODERATOR_ID = 1L;
        final Long DEPARTMENT_ID = 2L;
        try (MockedStatic<RoleService> utilities = Mockito.mockStatic(RoleService.class)){
            Department department = mock();
            User moderator = mock();
            Role adminRole = mock();

            doReturn(Optional.of(department)).when(departmentRepositories).findByIdWithPessimisticWRITE(DEPARTMENT_ID);
            doReturn(Optional.of(moderator)).when(userRepositories).findById(MODERATOR_ID);

            doReturn(userRole).when(moderator).getRole();
            doReturn(mock(Department.class)).when(department).getDepartmentParent();
            doReturn(department).when(moderator).getDepartment();

            doReturn(DEPARTMENT_ID).when(department).getId();
            //doReturn("egor").when(moderator).getUsername();
            //doReturn("BACKEND - департамент").when(department).getName();

            doReturn(true).when(departmentRepositories).departmentIsSubDepartmentOf(anyLong(),anyLong());

            utilities.when(() ->RoleService.getADMIN_ROLE()).thenReturn(adminRole);

            //WHEN
            departmentServiceUnderTest.setDepartmentModerator(MODERATOR_ID, DEPARTMENT_ID);
            //THEN
            verify(department, times(1)).setModerator(moderator);
        }
    }

    @Test
    void tryToSetDepartmentBoss()
    {
        //GIVING
        final Long BOSS_ID = 1L;
        final Long DEPARTMENT_ID = 2L;
        try (MockedStatic<RoleService> utilities = Mockito.mockStatic(RoleService.class)){
            Department department = mock();
            User boss = mock();
            Role adminRole = mock();

            doReturn(Optional.of(department)).when(departmentRepositories).findByIdWithPessimisticREAD(DEPARTMENT_ID);
            doReturn(Optional.of(boss)).when(userRepositories).findByIdPessimisticLockRead(BOSS_ID);

            doReturn(userRole).when(boss).getRole();
            doReturn(mock(Department.class)).when(department).getDepartmentParent();
            //doReturn(department).when(boss).getDepartment();
            doReturn(mock(User.class)).when(department).getModerator();

            utilities.when(() ->RoleService.getADMIN_ROLE()).thenReturn(adminRole);

            //WHEN
            departmentServiceUnderTest.setDepartmentBoss(BOSS_ID, DEPARTMENT_ID);
            //THEN
            verify(department, times(1)).setBoss(boss);
        }
    }
}