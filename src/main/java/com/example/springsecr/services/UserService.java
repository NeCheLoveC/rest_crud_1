package com.example.springsecr.services;

import com.example.springsecr.dto.converter.UserRegisterRequestConverter;
import com.example.springsecr.dto.model.request.user.UserRegisterCredentionalsRequestDto;
import com.example.springsecr.dto.model.request.user.UserUpdateRequestDTO;
import com.example.springsecr.exceptions.BadRequestException;
import com.example.springsecr.exceptions.HttpCustomException;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.RoleRepositories;
import com.example.springsecr.repositories.UserRepositories;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import com.example.springsecr.validators.UserRegistrationDtoValidator;
import com.example.springsecr.validators.UserUpdateDtoValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.atn.EmptyPredictionContext;
import org.hibernate.query.sqm.tree.predicate.SqmInListPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor()
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

    @PersistenceContext
    private EntityManager entityManager;

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
    public User saveUser(UserRegisterCredentionalsRequestDto newUser)
    {
        BindingResult bindingResult1 = new DirectFieldBindingResult(newUser, "newUser");
        userRegistrationDtoValidator.validate(newUser, bindingResult1);
        if(bindingResult1.hasErrors())
            throw new HttpCustomException(HttpStatus.BAD_REQUEST,bindingResult1.getAllErrors().stream().map(er -> er.getObjectName() + " : " + er.getDefaultMessage()).collect(Collectors.joining()));
        User user = userRegisterConverter.apply(newUser);
        User returnedUser = userRepo.save(user);
        return returnedUser;
    }

    @Transactional
    public User update(UserUpdateRequestDTO userUpdateDTO)
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
        if(Objects.nonNull(departmentId))
        {
            Optional<Department> department = departmentRepositories.findById(departmentId);
            department.orElseThrow(() -> new BadRequestException("Департамент с id = %s не найден"));
            user.get().setDepartment(department.get());
        }
        else
        {
            user.get().setDepartment(null);
        }
        return user.get();
    }

    public Collection<User> findUsersByPredicate(String username, String email, String position)
    {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = cb.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        Predicate predicate = null;
        Predicate partOfConditional = null;
        if(username != null)
        {
            predicate = cb.like(root.get("username"),username + "%");
        }
        if(email != null)
        {
            partOfConditional = cb.like(root.get("email"),email + "%");
            if(predicate == null)
                predicate = partOfConditional;
            else
                predicate = cb.and(predicate, partOfConditional);
        }
        if(position != null)
        {
            partOfConditional = cb.like(root.get("position"),position);
            if(predicate == null)
                predicate = partOfConditional;
            else
                predicate = cb.and(predicate, partOfConditional);
        }

        if(predicate != null)
            criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
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
