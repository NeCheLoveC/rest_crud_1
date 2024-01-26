package com.example.springsecr.repositories;

import com.example.springsecr.models.Department;
import com.example.springsecr.models.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface DepartmentRepositories extends JpaRepository<Department, Long>
{
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Transactional
    @Query("select d from Department d where d.id = :id")
    public Optional<Department> findByIdWithPessimisticREAD(long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Query("select d from Department d where d.id = :id")
    public Optional<Department> findByIdWithPessimisticWRITE(long id);

    @Transactional
    @Query("select count(*) from Department")
    public long getCountEntities();

    @Transactional
    @Query("select d from Department d where d.isDeleted = false")
    public Collection<Department> getAllActiveDepartments();

    @Override
    @Transactional
    @Query("select d from Department d where d.id = :id and d.isDeleted = false")
    public Optional<Department> findById(Long id);

    @Transactional
    @Query("select d from Department d where d.departmentParent is null")
    public Department findRootDepartment();

    @Transactional
    @Query("select isSubDepartment(:department, :childDepartment)")
    public boolean departmentIsSubDepartmentOf(long department, long childDepartment);
}
