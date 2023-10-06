package com.example.springsecr.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("singleton")
public class JwtUtils
{
    @Value("${application.security.jwt.secret}")
    protected String secretKey;
    protected UserDetailsService userDetailsService;
    protected Long timeExpired = 1000L * 60L * 30L;

    @Autowired
    public JwtUtils(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    //Генерация токена по UserDetails
    public String generateJwt(UserDetails userDetails)
    {
        Date currentDate = new Date();
        Claims claims = Jwts.claims();
        claims.setSubject(userDetails.getUsername());
        claims.setIssuedAt(currentDate);
        Date expiredAt = new Date(currentDate.getTime() + timeExpired);
        claims.setExpiration(expiredAt);
        //Payload
        Map<String, Object> mapClaims = new HashMap<>()
        {
            {
                put("authorities", userDetails.getAuthorities().stream().map(auth -> auth.getAuthority()).toList());
            }
        };
        claims.putAll(mapClaims);
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public Claims getClaimsFromToken(String token)
    {
        return Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(token).getBody();
    }



}
