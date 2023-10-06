package com.example.springsecr.exceptions;

public class UsernameAlreadyExist extends RuntimeException
{
    public UsernameAlreadyExist(String mes)
    {
        super(mes);
    }
}
