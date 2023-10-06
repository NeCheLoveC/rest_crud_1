package com.example.springsecr.repositories;

import com.example.springsecr.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepositories extends JpaRepository<Department, Long>
{

}
