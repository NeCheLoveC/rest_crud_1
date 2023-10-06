package com.example.springsecr.repositories;

import com.example.springsecr.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositories extends JpaRepository<User, Long>
{
    @Query("from User u where u.username = :username")
    public Optional<User> getUserByUsername(String username);

    @Query("select count(*) from User")
    public long count();
    public Optional<User> getUserByEmail(String email);
}
