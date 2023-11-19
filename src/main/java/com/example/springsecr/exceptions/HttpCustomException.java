package com.example.springsecr.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class HttpCustomException extends RuntimeException
{
    @Getter
    HttpStatus httpStatus;
    @Getter
    protected String message;
    public HttpCustomException(HttpStatus httpStatus)
    {
        super();
        this.httpStatus = httpStatus;
    }
    public HttpCustomException(HttpStatus httpStatus, String message)
    {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public HttpCustomException(HttpStatus httpStatus, BindingResult bindingResult)
    {
        this(httpStatus);
        if(bindingResult == null)
            throw new RuntimeException("bindingResult не должен равняться null");
        this.message = convertBindingResultToMap(bindingResult).toString();
    }

    private String convertBindingResultToMap(BindingResult bindingResult)
    {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().stream().peek(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return errors.toString();
    }



}
