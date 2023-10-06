package com.example.springsecr.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptEncoderWrapper
{
    protected BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public BCryptPasswordEncoder getbCryptEncoderWrapper() {
        return bCryptPasswordEncoder;
    }
}
