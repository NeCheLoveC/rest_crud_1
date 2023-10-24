package com.example.springsecr.services;

import com.example.springsecr.dto.converter.UserRegisterRequestConverter;
import com.example.springsecr.dto.model.UserRegisterCredentionalsRequestDto;
import com.example.springsecr.dto.model.UserUpdateDTO;
import com.example.springsecr.exceptions.BadRequestException;
import com.example.springsecr.exceptions.UsernameAlreadyExist;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.RoleRepositories;
import com.example.springsecr.repositories.UserRepositories;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import com.example.springsecr.validators.UserRegistrationDtoValidator;
import com.example.springsecr.validators.UserUpdateDtoValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserService implements UserDetailsService
{
    private DepartmentRepositories departmentRepositories;
    private RoleService roleService;
    private UserRepositories userRepo;
    private RoleRepositories roleRepo;
    private BCryptEncoderWrapper bCryptPasswordWrapper;
    private UserRegisterRequestConverter userRegisterConverter;
    private UserRegistrationDtoValidator userRegistrationDtoValidator;
    private UserUpdateDtoValidator userUpdateDtoValidator;

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

    @Transactional()
    public User saveUser(UserRegisterCredentionalsRequestDto newUser) throws UsernameAlreadyExist
    {
        BindingResult bindingResult1 = new DirectFieldBindingResult(newUser, "newUser");
        userRegistrationDtoValidator.validate(newUser, bindingResult1);
        if(bindingResult1.hasErrors())
            throw new BadRequestException(bindingResult1.getAllErrors().stream().map(er -> er.getObjectName() + " : " + er.getDefaultMessage()).collect(Collectors.joining()));
        User user = userRegisterConverter.apply(newUser);
        User returnedUser = userRepo.save(user);
        return returnedUser;
    }

    @Transactional
    public User update(UserUpdateDTO userUpdateDTO)
    {
        Optional<User> userWrapper = userRepo.findById(userUpdateDTO.getId());
        BindingResult bindingResult = new DirectFieldBindingResult(userUpdateDTO, "userUpdateDTO");
        userUpdateDtoValidator.validate(userUpdateDTO, bindingResult);
        User user = userRepo.findById(userUpdateDTO.getId()).get();
        user.setEmail(userUpdateDTO.getEmail());
        user.setPassword(bCryptPasswordWrapper.getbCryptEncoderWrapper().encode(userUpdateDTO.getPassword()));
        return user;
    }

    @Transactional
    public User setDepartment(Long userId, Long departmentId)
    {
        Optional<User> user = userRepo.findById(userId);
        user.orElseThrow(() -> new BadRequestException("Пользователь с id = %s не найден".formatted(userId)));
        Optional<Department> department = departmentRepositories.findById(departmentId);
        department.orElseThrow(() -> new BadRequestException("Департамент с id = %s не найден"));
        user.get().setDepartment(department.get());
        return user.get();
    }

    public Optional<User> findById(Long id)
    {
        return userRepo.findById(id);
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
