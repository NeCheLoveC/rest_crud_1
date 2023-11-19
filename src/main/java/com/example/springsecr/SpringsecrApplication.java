package com.example.springsecr;

import com.example.springsecr.dto.converter.UserRegisterRequestConverter;
import com.example.springsecr.dto.model.request.user.UserRegisterCredentionalsRequestDto;
import com.example.springsecr.services.DepartmentService;
import com.example.springsecr.services.RoleService;
import com.example.springsecr.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
    public CommandLineRunner init(RoleService roleService, UserService userService, DepartmentService departmentService)
    {
        return (args) ->
        {
            //Администратор системы - 1 человек
            if(userService.getCountUser() == 0)
            {
                createFirstUserAdmin();
            }
            if(departmentService.count() == 0)
            {
                //init корневой департамент
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
