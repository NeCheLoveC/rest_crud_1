package com.example.springsecr;

import com.example.springsecr.models.Department;
import com.example.springsecr.models.User;
import com.example.springsecr.services.DepartmentService;
import com.example.springsecr.services.RoleService;
import com.example.springsecr.services.UserService;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class InitUtil
{
    protected RoleService roleService;
    protected UserService userService;
    protected DepartmentService departmentService;
    protected BCryptEncoderWrapper bCryptEncoderWrapper;

    @PersistenceContext
    protected EntityManager entityManager;
    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void init()
    {
        roleService.init();
        if(departmentService.count() == 0)
        {
            createRootDepartment();
            Department rootDepartment = departmentService.getRootDepartment();
            createFirstUserAdmin(rootDepartment,bCryptEncoderWrapper);
            User admin = userService.getAdmin().get();
            setBossAtRootDepartment(rootDepartment, admin);
        }
    }

    private void createRootDepartment()
    {
        String departmentName = "Главный отдел компании 'Рога и Копыта'";
        Query query = entityManager.createNativeQuery("insert into department(id,name) values (nextval('department_generator_id_seq'),:name)");
        query.setParameter("name", departmentName);
        query.executeUpdate();
    }

    private void createFirstUserAdmin(Department rootDepartment, BCryptEncoderWrapper bCryptEncoderWrapper)
    {
        String adminUsername = "admin";
        String adminPass = bCryptEncoderWrapper.getbCryptEncoderWrapper().encode("admin");
        String adminEmail = "adminprot@mail.ru";
        String adminPosition = departmentService.BOSS_POSITION;

        Query query = entityManager.createNativeQuery("insert into users(id,username, password, email, role_id, department_id, position) values (nextval('users_sequence_gen'),:username, :pass, :email, :role, :department_id, :position)");
        query.setParameter("username", adminUsername);
        query.setParameter("pass", adminPass);
        query.setParameter("email", adminEmail);
        query.setParameter("role", RoleService.getADMIN_ROLE().getId());
        query.setParameter("department_id", rootDepartment.getId());
        query.setParameter("position",adminPosition);
        query.executeUpdate();
    }

    private void setBossAtRootDepartment(Department department, User admin)
    {
        Query query = entityManager.createNativeQuery("insert into department_moderator (user_id, department_id) values (:department_id,:user_id)");
        query.setParameter("user_id", admin.getId());
        query.setParameter("department_id", department.getId());

        query.executeUpdate();

        query = entityManager.createNativeQuery("insert into department_boss (boss_id, department_id) values (:department_id,:user_id)");
        query.setParameter("user_id", admin.getId());
        query.setParameter("department_id", department.getId());
        query.executeUpdate();
    }
}
