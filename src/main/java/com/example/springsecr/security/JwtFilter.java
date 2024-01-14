package com.example.springsecr.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.Lifecycle;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
public class JwtFilter extends OncePerRequestFilter
{
    protected String prefix = "Bearer ";

    protected JwtUtils jwtUtils;

    @Autowired
    public JwtFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;
        Claims claims = null;

        if(authHeader != null && authHeader.startsWith(prefix))
        {
            token = authHeader.substring(prefix.length());
            try
            {
                claims = jwtUtils.getClaimsFromToken(token);
                username = claims.getSubject();
            }
            catch (ExpiredJwtException e)
            {
                System.out.println("Время жизни ТОКЕНА вышло.");
            }
            catch (SignatureException e)
            {
                System.out.println("Ошибка в СИГНАТУРЕ токена");
            }
        }


        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null && claims != null)
        {
            Authentication authToken = new UsernamePasswordAuthenticationToken(username,null,((List<String>) claims.get("authorities")).stream().map(
                    authGrand -> new SimpleGrantedAuthority(authGrand)
            ).toList());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request,response);
    }
}
