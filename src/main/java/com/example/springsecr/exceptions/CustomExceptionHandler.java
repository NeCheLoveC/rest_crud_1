package com.example.springsecr.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class CustomExceptionHandler
{
    @ExceptionHandler({BadRequestException.class, ConstraintViolationException.class})
    public ResponseEntity<?> constraintViolation(RuntimeException err)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap.SimpleImmutableEntry<String,String>("error_message",err.getMessage()));
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<?> notFoundUser(RuntimeException err)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new HashMap.SimpleImmutableEntry<String,String>("error_message",err.getMessage()));
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<?> notFoundEntity(RuntimeException er)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap.SimpleImmutableEntry<String,String>("error_message",er.getMessage()));
    }

    @ExceptionHandler({HttpCustomException.class})
    public ResponseEntity<?> catchCustomException(HttpCustomException err)
    {
        return ResponseEntity.status(err.getHttpStatus()).body(new HashMap.SimpleImmutableEntry<String,String>("error_message",err.getMessage()));
    }
}
