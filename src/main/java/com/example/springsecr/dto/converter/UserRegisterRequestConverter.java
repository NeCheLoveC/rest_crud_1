package com.example.springsecr.dto.converter;

import com.example.springsecr.dto.model.request.user.UserRegisterCredentionalsRequestDto;
import com.example.springsecr.models.Role;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.models.User;
import com.example.springsecr.services.RoleService;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class UserRegisterRequestConverter implements Function<UserRegisterCredentionalsRequestDto, User>
{
    private BCryptEncoderWrapper bCryptEncoderWrapper;
    private RoleService roleService;

    @Override
    public User apply(UserRegisterCredentionalsRequestDto object) {
        RoleType userRole = RoleType.valueOf(object.getRole().substring(RoleType.ROLE_SUFFIX.length()));
        Collection<Role> usersAuthorities = Collections.singletonList(roleService.getRoleEntityByRoleEnum(userRole));
        User user = new User(object.getUsername(),bCryptEncoderWrapper.getbCryptEncoderWrapper().encode(object.getPassword()), object.getEmail(), usersAuthorities);
        return user;
    }
}
