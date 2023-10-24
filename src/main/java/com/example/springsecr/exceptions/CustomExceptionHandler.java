package com.example.springsecr.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler
{
    @ExceptionHandler({UsernameAlreadyExist.class, BadRequestException.class})
    public ResponseEntity<String> constraintViolation(RuntimeException err)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err.getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<String> notFoundUser(RuntimeException err)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<String> notFoundEntity(RuntimeException er)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er.getMessage());
    }
}
