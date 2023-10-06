package com.example.springsecr.controllers;

import com.example.springsecr.dto.model.UsernamePasswordDTO;
import com.example.springsecr.models.User;
import com.example.springsecr.security.JwtUtils;
import com.example.springsecr.services.UserService;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/jwt")
@AllArgsConstructor
public class TokenController
{
    private UserService userService;
    private JwtUtils jwtUtils;
    private BCryptEncoderWrapper bCryptEncoderWrapper;

    @PostMapping
    public ResponseEntity<?> getJWT(@Valid @RequestBody UsernamePasswordDTO usernamePasswordDTO, BindingResult bindingResult)
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

    private boolean authorizationIsSuccess(UserDetails user, UsernamePasswordDTO credentials)
    {
        BCryptPasswordEncoder passwordEncoder = bCryptEncoderWrapper.getbCryptEncoderWrapper();
        return user.getUsername().equals(credentials.getUsername()) && passwordEncoder.matches(credentials.getPassword(),user.getPassword());
    }
}
