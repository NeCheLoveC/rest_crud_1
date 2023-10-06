package com.example.springsecr;

import com.example.springsecr.dto.ConverterDTO;
import com.example.springsecr.dto.model.UserRegisterCredentionalsDto;
import com.example.springsecr.models.User;
import com.example.springsecr.services.RoleService;
import com.example.springsecr.services.UserService;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

@SpringBootApplication
@AllArgsConstructor
public class SpringsecrApplication {


    protected RoleService roleService;
    protected UserService userService;
    protected ConverterDTO<UserRegisterCredentionalsDto, User> converterUserRegisterDto;

    public static void main(String[] args) {
        SpringApplication.run(SpringsecrApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(RoleService roleService, UserService userService)
    {
        return (args) ->
        {
            //Администратор системы - 1 человек
            if(userService.getCountUser() == 0)
            {
                createFirstUserAdmin();
            }
        };
    }

    private void createFirstUserAdmin()
    {
        User user = new User("admin","admin","adminprot@mail.ru", Collections.singleton(roleService.getADMIN_ROLE()));
        UserRegisterCredentionalsDto adminDTO = converterUserRegisterDto.convertToDTO(user);
        userService.saveUser(adminDTO);
    }

}
