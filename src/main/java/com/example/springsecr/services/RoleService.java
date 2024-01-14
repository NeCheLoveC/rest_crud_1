package com.example.springsecr.services;

import com.example.springsecr.models.Role;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.repositories.RoleRepositories;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService
{
    protected final RoleRepositories roleRepositories;
    private static Role USER_ROLE;
    private static Role MODERATOR_ROLE;
    private static Role ADMIN_ROLE;

    public void init()
    {
        //Получаем синглтон-отображения каждой роли (закешировать каждую роль), если данной роли нет в БД - создаем ее
        roleRepositories.getRoleByName(RoleType.USER.getRoleName()).ifPresentOrElse(t -> this.USER_ROLE = t, () -> USER_ROLE = roleRepositories.save(new Role(RoleType.USER.getRoleName())));
        roleRepositories.getRoleByName(RoleType.MODERATOR.getRoleName()).ifPresentOrElse(t -> this.MODERATOR_ROLE = t, () -> MODERATOR_ROLE = roleRepositories.save(new Role(RoleType.MODERATOR.getRoleName())));
        roleRepositories.getRoleByName(RoleType.ADMIN.getRoleName()).ifPresentOrElse(t -> this.ADMIN_ROLE = t, () -> ADMIN_ROLE = roleRepositories.save(new Role(RoleType.ADMIN.getRoleName())));
    }


    public Role createRole(String role)
    {
        return roleRepositories.save(new Role(role));
    }

    public Optional<Role> getRoleByName(String roleName)
    {
        return roleRepositories.getRoleByName(roleName);
    }

    public static Role getUSER_ROLE() {
        return USER_ROLE;
    }

    public static Role getMODERATOR_ROLE() {
        return MODERATOR_ROLE;
    }

    public static Role getADMIN_ROLE() {
        return ADMIN_ROLE;
    }

    public static Role getRoleEntityByRoleEnum(RoleType roleType)
    {
        switch (roleType)
        {
            case USER:
                return USER_ROLE;
            case MODERATOR:
                return MODERATOR_ROLE;
            case ADMIN:
                return ADMIN_ROLE;
            default:
                throw new IllegalArgumentException("Роль " + roleType.getRoleName() + "не поддерживается...");
        }
    }
}
