package com.example.springsecr.services;

import com.example.springsecr.dto.converter.UserRegisterConverter;
import com.example.springsecr.dto.model.UserRegisterCredentionalsDto;
import com.example.springsecr.exceptions.BadRequestException;
import com.example.springsecr.exceptions.Test;
import com.example.springsecr.exceptions.UsernameAlreadyExist;
import com.example.springsecr.models.Role;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.RoleRepositories;
import com.example.springsecr.repositories.UserRepositories;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import com.example.springsecr.validators.UserRegistrationDtoValidator;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserService implements UserDetailsService
{
    private RoleService roleService;
    private UserRepositories userRepo;
    private RoleRepositories roleRepo;
    private BCryptEncoderWrapper bCryptPasswordWrapper;
    private UserRegisterConverter userRegisterConverter;
    protected UserRegistrationDtoValidator userRegistrationDtoValidator;

    public Optional<User> getUserByUsername(String username)
    {
        return userRepo.getUserByUsername(username);
    }

    public User createDefaultUser(String username,String password)
    {
        return null;
    }

    public User createAdmin(String username,String password)
    {
        return null;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.getUserByUsername(username);
        user.orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь '%s' не найден",username)));
        UserDetails userDetails = convertUserToUserDetails(user.get());
        return userDetails;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User saveUser(UserRegisterCredentionalsDto newUser) throws UsernameAlreadyExist
    {
        BindingResult bindingResult1 = new DirectFieldBindingResult(newUser, "newUser");
        userRegistrationDtoValidator.validate(newUser, bindingResult1);
        if(bindingResult1.hasErrors())
            throw new BadRequestException(bindingResult1.getAllErrors().stream().map(er -> er.getDefaultMessage()).collect(Collectors.joining()));
        User user = userRegisterConverter.convertedToEntity(newUser);
        return userRepo.save(user);
    }

    public Optional<User> getUserByEmail(String email)
    {
        return userRepo.getUserByEmail(email);
    }

    private UserDetails convertUserToUserDetails(User user)
    {
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(), user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList());
    }

    public long getCountUser()
    {
        return userRepo.count();
    }

}
