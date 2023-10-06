package com.example.springsecr.controllers;

import com.example.springsecr.dto.model.UserRegisterCredentionalsDto;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.services.UserService;
import com.example.springsecr.validators.UserRegistrationDtoValidator;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController
{
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> register(
            @RequestBody @Valid UserRegisterCredentionalsDto credentionals,
            BindingResult result,
            Authentication authentication)
    {
        if(result.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors().stream().map(k -> k.getObjectName()).collect(Collectors.toUnmodifiableList()));
        if(credentionals.getRole().equals(RoleType.MODERATOR.getRoleName()))
        {
            //Проверяем, есть ли у отправителя доступ для создания новых User с ролью "Модератор"
            if(authentication == null)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У вас нет прав создавать модераторов");
            if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().
                    noneMatch(
                    (i) -> i.getAuthority().equals(RoleType.ADMIN.getRoleName())
                    )
            )
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У вас не прав создавать модераторов");
            }
        }
        userService.saveUser(credentionals);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
