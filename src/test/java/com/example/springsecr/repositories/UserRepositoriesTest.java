package com.example.springsecr.repositories;


import com.example.springsecr.InitSql;
import com.example.springsecr.SpringsecrApplication;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.Role;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.models.User;
import com.example.springsecr.services.RoleService;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.event.BeforeTestExecutionEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@Import({InitSql.class, BCryptEncoderWrapper.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
class UserRepositoriesTest {
    @Autowired
    private UserRepositories userRepositoriesUnderTest;
    @Autowired
    private InitSql initSql;
    @Autowired
    private DepartmentRepositories departmentRepositories;
    @Autowired
    private RoleRepositories roleRepositories;

    @BeforeAll
    public void initIfDataBaseIsPure()
    {
        initSql.init();
    }

    @Test
    void itCheckThatUserExistByUsername()
    {
        String username = "admin";
        Optional<User> user = userRepositoriesUnderTest.getUserByUsername(username);

        assertThat(user.isPresent()).as("Проверка наличия в системе пользователя с username = %s", username).isTrue();
        assertThat(user.get().getUsername()).as("Ожидался username = %s", username).isEqualTo("admin");
    }

    @Test
    void itCheckThatUserNotExistByUsername()
    {
        String username = "admina";

        Optional<User> user = userRepositoriesUnderTest.getUserByUsername(username);

        assertThat(user.isPresent()).as("Проверка наличия в системе пользователя с username = %s", username).isFalse();
    }

    @Test
    void itCheckCountUsersIntoPureSystemIsEqualTo1()
    {
        //В система по-умолчанию существует только один пользователь - Администратор системы
        assertThat(userRepositoriesUnderTest.count()).isEqualTo(1);
    }

    @Test
    void itCheckThatUserExistByEmail()
    {
        String userEmail = "aDminProt@mail.ru";

        Optional<User> user = userRepositoriesUnderTest.getUserByEmail(userEmail);

        assertThat(user.isPresent()).as("Проверка наличия в системе пользователя с email = %s", userEmail).isTrue();
        assertThat(user.get().getEmail()).as("Найденный пользоваель должен иметь email = %s", userEmail).isEqualToIgnoringCase(userEmail);
    }

    @Test
    void itCheckThatUserNotExistByEmail()
    {
        String userEmail = "NotExistEmail@mail.ru";

        Optional<User> user = userRepositoriesUnderTest.getUserByEmail(userEmail);

        assertThat(user.isPresent()).as("Проверка наличия в системе пользователя с email = %s", userEmail).isFalse();
    }


    @Test
    void canGetAdmin()
    {
        Optional<User> admin = userRepositoriesUnderTest.getAdmin();
        assertThat(admin.isPresent()).as("Администратор системы всегда должен присутствовать в системе").isTrue();
        assertThat(admin.get().getRole().getName()).as("Администратор системы должен иметь роль = %s", RoleType.ADMIN.getRoleName()).isEqualTo(RoleType.ADMIN.getRoleName());
        assertThat(admin.get().getUsername()).as("Администрато должен иметь username = %s", "admin").isEqualTo("admin");
    }

    @Test
    @Transactional
    void getEmployersByDepartmentId()
    {
        //Корневой департамент состоит из двух пользователей
        Role role = roleRepositories.getRoleByName(RoleType.USER.getRoleName()).get();
        Department rootDepartment = departmentRepositories.findRootDepartment();
        User userIntoRootDepartment = new User("anton", "123456","anto@gmail.com",role,rootDepartment, "Java-Junior");
        userRepositoriesUnderTest.save(userIntoRootDepartment);

        //Создание дочернего департамента и закрепление за ним босса

        Department newDepartment = new Department("Frontend - департамент", rootDepartment);
        departmentRepositories.save(newDepartment);

        User userIntoFrontendDepartment = new User("Eugene", "123456","Eugene@gmail.com", role,newDepartment);
        userRepositoriesUnderTest.save(userIntoFrontendDepartment);

        newDepartment.setBoss(userIntoFrontendDepartment);

        Collection<User> usersIntoDepartment = userRepositoriesUnderTest.getEmployersByDepartmentId(rootDepartment.getId());
        assertThat(usersIntoDepartment).hasSize(2).allMatch(u -> u.getDepartment().equals(rootDepartment)).doesNotContain(userIntoFrontendDepartment);
    }
}