package com.example.springsecr.dto.converter;

import com.example.springsecr.dto.ConverterDTO;
import com.example.springsecr.dto.model.UserRegisterCredentionalsDto;
import com.example.springsecr.models.Role;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.models.User;
import com.example.springsecr.services.RoleService;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserRegisterConverter implements ConverterDTO<UserRegisterCredentionalsDto, User>
{
    private BCryptEncoderWrapper bCryptEncoderWrapper;
    private RoleService roleService;
    @Override
    public UserRegisterCredentionalsDto convertToDTO(User object) {
        UserRegisterCredentionalsDto userDto = new UserRegisterCredentionalsDto();
        userDto.setUsername(object.getUsername());
        userDto.setPassword(object.getPassword());
        userDto.setEmail(object.getEmail());
        userDto.setRole(object.getRoles().stream().filter((i) -> i.getName().startsWith("ROLE_")).findFirst().get().getName());
        return userDto;
    }

    @Override
    public User convertedToEntity(UserRegisterCredentionalsDto object) {
        RoleType userRole = RoleType.valueOf(object.getRole().substring(RoleType.ROLE_SUFFIX.length()));
        Collection<Role> usersAuthorities = Collections.singletonList(roleService.getRoleEntityByRoleEnum(userRole));
        User user = new User(object.getUsername(),bCryptEncoderWrapper.getbCryptEncoderWrapper().encode(object.getPassword()), object.getEmail(), usersAuthorities);
        return user;
    }
}
