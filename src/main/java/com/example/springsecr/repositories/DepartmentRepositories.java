package com.example.springsecr.repositories;

import com.example.springsecr.models.Department;
import com.example.springsecr.models.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface DepartmentRepositories extends JpaRepository<Department, Long>
{
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select d from Department d where d.id = :id")
    @Transactional
    public Optional<Department> findByIdWithPessimisticREAD(long id);

    @Transactional
    @Query("select d from Department d where d.moderator = :admin")
    public Optional<Department> findDepartmentByAdminId(User admin);
}
