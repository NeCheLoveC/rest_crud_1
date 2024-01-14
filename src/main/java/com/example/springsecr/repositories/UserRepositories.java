package com.example.springsecr.repositories;

import com.example.springsecr.models.Department;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.models.User;
import jakarta.persistence.LockModeType;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.OptimisticLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepositories extends JpaRepository<User, Long>
{
    @Query("from User u where u.username = :username")
    @Transactional
    public Optional<User> getUserByUsername(String username);

    @Query("select count(*) from User")
    @Transactional
    public long count();
    @Transactional
    public Optional<User> getUserByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select u from User u where u.id = :userId")
    @Transactional
    public Optional<User> findByIdPessimisticLockRead(Long userId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query("select u from User u where u.id = :userId")
    public Optional<User> findByIdPessimisticLockWrite(Long userId);

    @Transactional
    @Query("select u from User u where u.role.name like 'ROLE_ADMIN'")
    public Optional<User> getAdmin();

    @Query("select u from User u where u.department.id = :departmentId")
    public Collection<User> getEmployersByDepartmentId(Long departmentId);

    @Override
    @Transactional
    @Query("select u from User u where u.id = :id and u.isDeleted = false")
    public Optional<User> findById(Long id);
}
