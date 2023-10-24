package com.example.springsecr.repositories;

import com.example.springsecr.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepositories extends JpaRepository<Department, Long>
{
    @Query("select d from Department d where d.id = :id")
    public Optional<Department> findByIdWithPessimisticREAD(long id);
}
