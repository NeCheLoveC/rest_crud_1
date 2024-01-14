package com.example.springsecr;

import com.example.springsecr.dto.model.request.department.DepartmentCreateRequestDTO;
import com.example.springsecr.dto.model.request.user.UserRegisterCredentialsRequestDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.Optional;

@SpringBootApplication

public class SpringsecrApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringsecrApplication.class, args);
    }
}
