package com.example.springsecr;

import com.example.springsecr.dto.converter.UserRegisterRequestConverter;
import com.example.springsecr.dto.model.request.department.DepartmentCreateRequestDTO;
import com.example.springsecr.dto.model.request.user.UserRegisterCredentionalsRequestDto;
import com.example.springsecr.models.User;
import com.example.springsecr.services.DepartmentService;
import com.example.springsecr.services.RoleService;
import com.example.springsecr.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootApplication
@AllArgsConstructor
public class SpringsecrApplication {


    protected RoleService roleService;
    protected UserService userService;
    protected UserRegisterRequestConverter converterUserRegisterDto;

    public static void main(String[] args) {
        SpringApplication.run(SpringsecrApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner init(RoleService roleService, UserService userService, DepartmentService departmentService)
    {
        return (args) ->
        {
            roleService.init();
            //Администратор системы - 1 человек
            if(userService.getCountUser() == 0)
            {
                createFirstUserAdmin();
            }
            if(departmentService.count() == 0)
            {
                //init корневой департамент
                Optional<User> admin = userService.getAdmin();
                admin.orElseThrow(() -> new RuntimeException("Не создан Администратор системы"));
                DepartmentCreateRequestDTO department = new DepartmentCreateRequestDTO();
                department.setName("Главный отдел компании \"Рога и Копыта\"");
                department.setDepartmentParentId(null);
                department.setBossId(admin.get().getId());
                departmentService.create(department);
            }
        };
    }

    private void createFirstUserAdmin()
    {
        UserRegisterCredentionalsRequestDto dto = new UserRegisterCredentionalsRequestDto();
        dto.setUsername("admin");
        dto.setPassword("admin");
        dto.setEmail("adminprot@mail.ru");
        dto.setRole("ROLE_ADMIN");
        userService.saveUser(dto);
    }

}
