package com.example.springsecr;

import com.example.springsecr.models.Department;
import com.example.springsecr.models.Role;
import com.example.springsecr.models.RoleType;
import com.example.springsecr.models.User;
import com.example.springsecr.services.DepartmentService;
import com.example.springsecr.services.RoleService;
import com.example.springsecr.services.UserService;
import com.example.springsecr.utils.BCryptEncoderWrapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InitSql
{
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private BCryptEncoderWrapper bCryptEncoderWrapper;
    @Transactional
    public void init()
    {
        Query query = entityManager.createNativeQuery("insert into role(id,name) values (nextval('role_seq'), :userRole) , (nextval('role_seq'), :adminRole)");
        query.setParameter("userRole", RoleType.USER.getRoleName());
        query.setParameter("adminRole", RoleType.ADMIN.getRoleName());
        query.executeUpdate();
        createRootDepartment();
        Long idRootDepartment = (Long) entityManager.createNativeQuery("select d.id from department d where d.department_parent_id is null").getSingleResult();
        createFirstUserAdmin(idRootDepartment,bCryptEncoderWrapper);
        Long adminId = (Long) entityManager.createNativeQuery("select u.id from users u where u.username = 'admin'").getSingleResult();
        setBossAtRootDepartment(idRootDepartment, adminId);

    }

    private void createRootDepartment()
    {
        String departmentName = "Главный отдел компании 'Рога и Копыта'";
        Query query = entityManager.createNativeQuery("insert into department(id,name) values (nextval('department_generator_id_seq'),:name)");
        query.setParameter("name", departmentName);
        query.executeUpdate();
    }

    private void createFirstUserAdmin(Long rootDepartmentId, BCryptEncoderWrapper bCryptEncoderWrapper)
    {
        String adminUsername = "admin";
        String adminPass = bCryptEncoderWrapper.getbCryptEncoderWrapper().encode("admin");
        String adminEmail = "adminprot@mail.ru";
        String adminPosition = DepartmentService.BOSS_POSITION;
        Query query = entityManager.createNativeQuery("select id from role where name = :adminRole");
        query.setParameter("adminRole", RoleType.ADMIN.getRoleName());
        Long adminRoleId = (Long) query.getSingleResult();

        query = entityManager.createNativeQuery("insert into users(id,username, password, email, role_id, department_id, position) values (nextval('users_sequence_gen'),:username, :pass, :email, :role, :department_id, :position)");
        query.setParameter("username", adminUsername);
        query.setParameter("pass", adminPass);
        query.setParameter("email", adminEmail);
        query.setParameter("role", adminRoleId);
        query.setParameter("department_id", rootDepartmentId);
        query.setParameter("position",adminPosition);
        query.executeUpdate();
    }

    private void setBossAtRootDepartment(Long departmentId, Long adminId)
    {
        Query query = entityManager.createNativeQuery("insert into department_moderator (user_id, department_id) values (:department_id,:user_id)");
        query.setParameter("user_id", adminId);
        query.setParameter("department_id", departmentId);

        query.executeUpdate();

        query = entityManager.createNativeQuery("insert into department_boss (boss_id, department_id) values (:department_id,:user_id)");
        query.setParameter("user_id", adminId);
        query.setParameter("department_id", departmentId);
        query.executeUpdate();
    }
}
