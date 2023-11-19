package com.example.springsecr.controllers;

import com.example.springsecr.dto.model.request.user.UsernamePasswordRequestDTO;
import com.example.springsecr.security.JwtUtils;
import com.example.springsecr.services.UserService;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jwt")
@AllArgsConstructor
public class TokenController
{
    private UserService userService;
    private JwtUtils jwtUtils;
    private BCryptEncoderWrapper bCryptEncoderWrapper;

    @PostMapping
    public ResponseEntity<?> getJWT(@Valid @RequestBody UsernamePasswordRequestDTO usernamePasswordDTO, BindingResult bindingResult)
    {
        if(bindingResult.hasErrors())
        {
            return ResponseEntity.badRequest().body("Ошибка валидации");
        }
        UserDetails user = userService.loadUserByUsername(usernamePasswordDTO.getUsername());

        if(authorizationIsSuccess(user, usernamePasswordDTO))
        {
            String jwt = jwtUtils.generateJwt(user);
            return ResponseEntity.ok(jwt);
        }
        else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка в логине/пароле");
        }
    }

    private boolean authorizationIsSuccess(UserDetails user, UsernamePasswordRequestDTO credentials)
    {
        BCryptPasswordEncoder passwordEncoder = bCryptEncoderWrapper.getbCryptEncoderWrapper();
        return user.getUsername().equals(credentials.getUsername()) && passwordEncoder.matches(credentials.getPassword(),user.getPassword());
    }
}
