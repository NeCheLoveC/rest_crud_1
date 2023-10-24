package com.example.springsecr.controllers;

import com.example.springsecr.dto.model.UserRegisterCredentionalsRequestDto;
import com.example.springsecr.dto.model.UserUpdateDTO;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController
{
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> register(
            @RequestBody @Valid UserRegisterCredentionalsRequestDto credentionals,
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

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Valid @RequestBody UserUpdateDTO userUpdateDTO,
            BindingResult bindingResult,
            @PathVariable("id") long id
    )
    {
        userUpdateDTO.setId(id);
        if(bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().stream().map(er -> er.getDefaultMessage()).collect(Collectors.joining()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idUser}/departments/{idDepartment}")
    public ResponseEntity<?> setUserDepartment(@PathVariable("idUser") Long idUser, @PathVariable("idDepartment") Long idDepartment)
    {
        userService.setDepartment(idUser, idDepartment);
        return ResponseEntity.ok().build();
    }
}
