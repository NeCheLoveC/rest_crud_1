package com.example.springsecr.repositories;

import com.example.springsecr.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RoleRepositories extends JpaRepository<Role,Long>
{
    @Transactional
    public Optional<Role> getRoleByName(String name);
}
