package com.example.springsecr.repositories;

import com.example.springsecr.InitSql;
import com.example.springsecr.models.Department;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({InitSql.class, BCryptEncoderWrapper.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DepartmentRepositoriesTest
{
    @Autowired
    private DepartmentRepositories departmentRepositoriesUnderTest;
    @Autowired
    private InitSql initSql;
    @PersistenceContext
    EntityManager entityManager;

    @BeforeAll
    public void initIfDataBaseIsPure()
    {
        initSql.init();
    }

    @Test
    void getAllActiveDepartments()
    {

        //Добавляем департамент с пометкой "на удаление"
        Department departmentForDelete = new Department("Frontend - департмент", departmentRepositoriesUnderTest.findRootDepartment());
        departmentForDelete.setDeleted(true);
        departmentRepositoriesUnderTest.save(departmentForDelete);

        Collection<Department> onlyActiveDepartments = departmentRepositoriesUnderTest.getAllActiveDepartments();
        assertThat(onlyActiveDepartments)
                .doesNotContain(departmentForDelete)
                .hasSize(1)
                .allMatch(d -> !d.isDeleted())
                .contains(departmentRepositoriesUnderTest.findRootDepartment());
        assertThat(departmentRepositoriesUnderTest.count()).isEqualTo(2);
    }


    @Test
    void findRootDepartment()
    {
        Department rootDeaprtment = departmentRepositoriesUnderTest.findRootDepartment();
        assertThat(rootDeaprtment)
                .isNotNull()
                .matches(d -> d.getDepartmentParent() == null);
    }

    @Disabled
    @Test
    void departmentIsSubDepartmentOf()
    {
        String query = "CREATE OR REPLACE FUNCTION isSubDepartment(super_department_id bigint, child_department_id bigint) RETURNS bool\n" +
                "AS $$\n" +
                "DECLARE\n" +
                "    cur_sub_department_id bigint := child_department_id;\n" +
                "BEGIN\n" +
                "    IF child_department_id = super_department_id THEN\n" +
                "        RETURN true;\n" +
                "    END IF;\n" +
                "\n" +
                "    WHILE cur_sub_department_id IS NOT NULL LOOP\n" +
                "        SELECT d.department_parent_id INTO cur_sub_department_id \n" +
                "        FROM department d \n" +
                "        WHERE d.id = cur_sub_department_id;\n" +
                "\n" +
                "        IF cur_sub_department_id = super_department_id THEN\n" +
                "            RETURN true;\n" +
                "        END IF;\n" +
                "    END LOOP;\n" +
                "\n" +
                "    RETURN false;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";
        entityManager.createNativeQuery(query).executeUpdate();
        //GIVEN
        Department mainDepartment = new Department("BACKEND - гланый департамент", departmentRepositoriesUnderTest.findRootDepartment());
        departmentRepositoriesUnderTest.save(mainDepartment);
        Department subDepartment = new Department("Отдел #1 - BACKEND", mainDepartment);
        departmentRepositoriesUnderTest.save(subDepartment);

        //WHEN
        boolean result = departmentRepositoriesUnderTest.departmentIsSubDepartmentOf(mainDepartment.getId(), subDepartment.getId());

        //THEN
        assertThat(result).isTrue();
    }
}