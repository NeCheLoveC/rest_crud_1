package com.example.springsecr.exceptions;

public class BadRequestException extends RuntimeException
{
    public BadRequestException(String mes)
    {
        super(mes);
    }
}
