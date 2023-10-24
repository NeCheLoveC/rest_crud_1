package com.example.springsecr.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class BCryptEncoderWrapper
{
    protected BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public BCryptPasswordEncoder getbCryptEncoderWrapper() {
        return bCryptPasswordEncoder;
    }
}
