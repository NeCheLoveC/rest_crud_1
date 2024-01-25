package com.example.springsecr.models;

import com.example.springsecr.services.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void tryToSetBossWhenNewBossIsBossAndDepartmentHasBoss()
    {
        //GIVEN
        Department firstDepartment = new Department("Backend - департамент", rootDepartment);
        User bossOfFirstDepartment = new User("egor", "123456","test1@mail.ru" ,role,firstDepartment, "Java Backend Developer");
        firstDepartment.setBoss(bossOfFirstDepartment);

        User bossOfSecondDepartment = new User("konstantin", "123456","test2@mail.ru" ,role,firstDepartment, "Java Backend Developer");
        Department secondDepartment = new Department("Backend - департамент", rootDepartment);
        secondDepartment.setBoss(bossOfSecondDepartment);

        //WHEN
        secondDepartment.setBoss(bossOfFirstDepartment);


        assertThat(bossOfFirstDepartment.getBossBy()).isEqualTo(secondDepartment);
        assertThat(bossOfFirstDepartment.getDepartment()).isEqualTo(secondDepartment);
        assertThat(bossOfFirstDepartment.getPosition()).isEqualTo(DepartmentService.BOSS_POSITION);

        assertThat(bossOfSecondDepartment.isBoss()).isFalse();
        assertThat(bossOfSecondDepartment.getDepartment()).isEqualTo(secondDepartment);
        assertThat(bossOfSecondDepartment.getPosition()).isEqualTo("");

        assertThat(firstDepartment.getBoss()).isNull();
        assertThat(firstDepartment.getModerator()).isNull();

        assertThat(secondDepartment.getBoss()).isEqualTo(bossOfFirstDepartment);
        assertThat(secondDepartment.getModerator()).isNull();
    }

    @Test
    void testSetModeratorWhenNewModeratorIsNullAndCurrentModeratorIsNotNull() {
        // Arrange
        Department department = new Department("Департамент 1",rootDepartment);

        User existingModerator = new User("egor", "123456", "test1@mail.ru", role,department);
        department.setModerator(existingModerator);

        // Act
        department.setModerator(null);

        // Assert
        assertThat(department.getModerator()).isNull();
        assertThat(existingModerator.getModeratorBy()).isNull();
    }

    @Test
    void testSetEqualsModerator() {
        // Arrange
        Department department = new Department("Департамент 1",rootDepartment);
        User existingModerator = new User("egor", "123456", "test1@mail.ru", role,department);
        department.setModerator(existingModerator);

        // Act
        department.setModerator(existingModerator);

        // Assert
        assertThat(department.getModerator()).isEqualTo(existingModerator);
        assertThat(existingModerator.getModeratorBy()).isEqualTo(department);
        //verify(existingModerator, times(1)).setModeratorBy(null);
    }

    @Test
    void testSetNullWhenCurrentModeratorIsNull() {
        // Arrange
        Department department = new Department("Департамент 1",rootDepartment);

        // Act
        department.setModerator(null);

        // Assert
        assertThat(department.getModerator()).isNull();
    }

    @Test
    void testSetModeratorWhenNewModeratorIsNotNullAndDifferentFromCurrentModerator() {
        // Arrange
        User existingModerator = mock();
        User newModerator = mock();

        when(newModerator.getModeratorBy()).thenReturn(null); // Новый модератор не модерирует другой отдел
        when(existingModerator.getModeratorBy()).thenReturn(null); // Текущий модератор не модерирует другой отдел

        Department department = new Department("Департамент 1",rootDepartment);
        department.setModerator(existingModerator);

        // Act
        department.setModerator(newModerator);

        // Assert
        assertThat(newModerator).isEqualTo(department.getModerator());
        assertThat(existingModerator.getModeratorBy()).isNull();
        verify(existingModerator, times(1)).setModeratorBy(null);
        verify(newModerator, times(1)).setModeratorBy(department);
    }

    @Test
    void testSetModeratorWhenNewModeratorIsNotNullAndModeratesAnotherDepartment() {
        // Arrange

        Department firstDepartment = new Department("Департамент 1",rootDepartment);
        Department secondDepartment = new Department("Департамент 2", rootDepartment);
        User moderatorOfFirstDepartment = new User("egor", "123456", "test1@mail.ru", role,firstDepartment);
        User moderatorOfSecondDepartment = new User("konstantin", "123456", "test2@mail.ru", role,secondDepartment);


        firstDepartment.setModerator(moderatorOfFirstDepartment);
        secondDepartment.setModerator(moderatorOfSecondDepartment);

        // Act
        secondDepartment.setModerator(moderatorOfFirstDepartment);

        // Assert
        assertThat(moderatorOfSecondDepartment.getModeratorBy()).isNull();
        assertThat(firstDepartment.getModerator()).isNull();
        assertThat(moderatorOfFirstDepartment.getModeratorBy()).isEqualTo(secondDepartment);
        assertThat(secondDepartment.getModerator()).isEqualTo(moderatorOfFirstDepartment);

        //verify(moderatorOfFirstDepartment, times(1)).setModeratorBy(null);
        //verify(moderatorOfSecondDepartment.getModeratorBy(), times(1)).setModerator(null);
        //verify(moderatorOfSecondDepartment, times(1)).setModeratorBy(department);
    }
}