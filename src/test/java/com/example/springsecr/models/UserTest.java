package com.example.springsecr.models;

import com.example.springsecr.services.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserTest {
    @Mock
    private Role role;
    @Mock
    private Department rootDepartment;
    @Test
    void changeDepartmentWhenUserIsBoss()
    {
        Department department = new Department("Департамент BACKEND", rootDepartment);
        Department newDepartment = new Department("Департмент DevOps", rootDepartment);
        User bossDep = new User("egor", "123456","test1@mail.ru" ,role,department);
        User bossNewDep = new User("kons", "123456","test2@mail.ru" ,role,newDepartment);

        department.setBoss(bossDep);
        newDepartment.setBoss(bossNewDep);

        bossDep.setDepartment(newDepartment);
        //boss.setPosition("DevOps");

        assertThat(bossDep.getBossBy()).isNull();
        assertThat(bossDep.getDepartment()).isEqualTo(newDepartment);
        assertThat(bossDep.getPosition()).isEqualTo("");

        assertThat(department.getBoss()).isNull();
    }
}