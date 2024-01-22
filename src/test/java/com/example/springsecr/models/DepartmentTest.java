package com.example.springsecr.models;

import com.example.springsecr.services.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DepartmentTest {
    @Mock
    private Role role;
    @Mock
    private Department rootDepartment;
    @Test
    void setDepartment() {
    }

    @Test
    void setModeratorBy() {
    }


    @Test
    void tryToSetBossToDepartmentWithoutBoss()
    {
        Department department = new Department("Backend - департамент", rootDepartment);
        User user = new User("egor", "123456","test@mail.ru" ,role,department, "Java Backend Developer");


        department.setBoss(user);

        assertThat(department.getBoss()).isEqualTo(user);
        assertThat(department.getModerator()).isNull();
        assertThat(user.getDepartment()).isEqualTo(department);
        assertThat(user.getPosition()).isEqualTo(DepartmentService.BOSS_POSITION);
    }

    //Попытка установить НОВОГО босса вместо старого
    @Test
    void tryToChangeBossDepartment()
    {
        Department department = new Department("Backend - департамент", rootDepartment);
        User oldBoss = new User("egor", "123456","test1@mail.ru" ,role,department, "Java Backend Developer");
        department.setBoss(oldBoss);
        User newBoss = new User("konstantin", "123456","test2@mail.ru" ,role,department, "Java Backend Developer");

        department.setBoss(newBoss);

        assertThat(newBoss.getPosition()).isEqualTo(DepartmentService.BOSS_POSITION);
        assertThat(department.getBoss()).isEqualTo(newBoss);
        assertThat(newBoss.getDepartment()).isEqualTo(department);
        assertThat(newBoss.getBossBy()).isEqualTo(department);
        assertThat(newBoss.getModeratorBy()).isNull();



        assertThat(oldBoss.getPosition()).isEqualTo("");
        assertThat(department.getModerator()).isNull();
        assertThat(oldBoss.getDepartment()).isEqualTo(department);
        assertThat(oldBoss.getBossBy()).isNull();
        assertThat(oldBoss.getModeratorBy()).isNull();
    }

    //Перевод босса в другой отдел с сохранением должности
    @Test
    void transferBossToAnotherDepartmentWithoutBoss()
    {
        Department newDepartment = new Department("Департамент BACKEND", rootDepartment);
        Department oldDepartment = new Department("Отдел Backend №1", newDepartment);
        User boss = new User("egor", "123456","test1@mail.ru" ,role,oldDepartment);
        oldDepartment.setBoss(boss);

        newDepartment.setBoss(boss);

        assertThat(newDepartment.getBoss()).isEqualTo(boss);
        assertThat(newDepartment.getModerator()).isNull();


        assertThat(boss.getPosition()).isEqualTo(DepartmentService.BOSS_POSITION);
        assertThat(boss.getModeratorBy()).isNull();
        assertThat(boss.getBossBy()).isEqualTo(newDepartment);
        assertThat(boss.getDepartment()).isEqualTo(newDepartment);

        assertThat(oldDepartment.getBoss()).isNull();
        assertThat(oldDepartment.getModerator()).isNull();
    }

    @Test
    void tryToRemoveTheBossFromDepartment()
    {
        Department newDepartment = new Department("Департамент BACKEND", rootDepartment);
        User boss = new User("egor", "123456","test1@mail.ru" ,role,newDepartment);
        newDepartment.setBoss(boss);

        newDepartment.setBoss(null);

        assertThat(newDepartment.getBoss()).isNull();
        assertThat(newDepartment.getModerator()).isNull();


        assertThat(boss.getPosition()).isEqualTo("");
        assertThat(boss.getModeratorBy()).isNull();
        assertThat(boss.getBossBy()).isNull();
        assertThat(boss.getDepartment()).isEqualTo(newDepartment);
    }
}