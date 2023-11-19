package com.example.springsecr.controllers;

import com.example.springsecr.dto.converter.UserToUserResponseDTOConverter;
import com.example.springsecr.dto.model.request.user.UpdateUserDepartmentsDTO;
import com.example.springsecr.dto.model.request.user.UserRegisterCredentionalsRequestDto;
import com.example.springsecr.dto.model.request.user.UserUpdateRequestDTO;
import com.example.springsecr.dto.model.response.UserResponseDTO;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController
{
    private UserService userService;
    private UserToUserResponseDTOConverter userToUserResponseDTOConverter;

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
            @Valid @RequestBody UserUpdateRequestDTO userUpdateDTO,
            BindingResult bindingResult,
            @PathVariable("id") long id
    )
    {
        userUpdateDTO.setId(id);
        if(bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().stream().map(er -> er.getDefaultMessage()).collect(Collectors.joining()));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idUser}/department")
    public ResponseEntity<?> setUserDepartment(@PathVariable("idUser") Long idUser, @RequestBody UpdateUserDepartmentsDTO updateUserDepartmentsDTO)
    {
        userService.setDepartment(idUser, updateUserDepartmentsDTO.getDepartmentId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllUserByPredicate(@RequestParam(value = "username", required = false) String username,
                                                   @RequestParam(value = "email" , required = false) String email,
                                                   @RequestParam(value = "position", required = false) String position)
    {
        List<UserResponseDTO> users =  userService.findUsersByPredicate(username, email, position).stream().map(userToUserResponseDTOConverter).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
}
