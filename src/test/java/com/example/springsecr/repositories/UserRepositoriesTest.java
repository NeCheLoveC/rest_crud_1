package com.example.springsecr.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class UserRepositoriesTest {

    @Autowired
    private UserRepositories userRepositories;
    @Test
    void itShouldCheckIfExistUserByUsername()
    {

    }

    @Test
    void count() {
    }

    @Test
    void getUserByEmail() {
    }

    @Test
    void findByIdPessimisticLockRead() {
    }

    @Test
    void findByIdPessimisticLockWrite() {
    }

    @Test
    void getAdmin() {
    }

    @Test
    void getEmployersByDepartmentId() {
    }

    @Test
    void findById() {
    }
}