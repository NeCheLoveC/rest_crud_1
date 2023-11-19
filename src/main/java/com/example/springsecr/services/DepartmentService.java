package com.example.springsecr.services;

import com.example.springsecr.dto.model.request.department.DepartmentCreateRequestDTO;
import com.example.springsecr.dto.model.request.department.DepartmentUpdateRequestDto;
import com.example.springsecr.exceptions.BadRequestException;
import com.example.springsecr.exceptions.HttpCustomException;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.UserRepositories;
import com.example.springsecr.validators.DepartmentCreateDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentService
{
    private final DepartmentRepositories departmentRepositories;
    private final UserRepositories userRepositories;
    private static final String BOSS_POSITION = "Руководитель департамента";
    private UserService userService;

    private final DepartmentCreateDtoValidator departmentCreateDtoValidator;
    public Department create(DepartmentCreateRequestDTO departmentDto)
    {
        BindingResult bindingResult = new DirectFieldBindingResult(departmentDto, "departmentDto");
        departmentCreateDtoValidator.validate(departmentDto, bindingResult);
        if(bindingResult.hasErrors())
        {
            throw new BadRequestException(bindingResult.getAllErrors().stream().map((err) -> err.getDefaultMessage()).collect(Collectors.joining()));
        }
        Department department = new Department();
        department.setName(departmentDto.getName());
        if(departmentDto.getDepartmentParentId() != null)
        {
            department.setDepartmentParent(departmentRepositories.findById(departmentDto.getDepartmentParentId()).get());
        }
        if(departmentDto.getBossId() != null)
        {
            User boss = userRepositories.findById(departmentDto.getBossId()).get();
            department.setBoss(boss);
            department.setModerator(boss);
        }
        if(departmentDto.getModeratorId() != null)
        {
            department.setModerator(userRepositories.findById(departmentDto.getModeratorId()).get());
        }
        //department.set
        return departmentRepositories.save(department);
    }

    public Collection<Department> getAll()
    {
        return departmentRepositories.findAll();
    }

    public Department update(DepartmentUpdateRequestDto departmentUpdateDto)
    {
        Optional<Department> department = departmentRepositories.findByIdWithPessimisticREAD(departmentUpdateDto.getId());
        department.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND));
        department.get().setName(departmentUpdateDto.getName());
        return department.get();
    }

    public void delete(Long id)
    {
        Optional<Department> wrapperDepartment = departmentRepositories.findByIdWithPessimisticREAD(id);
        wrapperDepartment.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND));

        Department department = wrapperDepartment.get();
        if(department.getDepartments().size() > 0)
        {
            for(Department d : department.getDepartments())
                d.setDepartmentParent(department.getDepartmentParent());
        }
        departmentRepositories.delete(department);
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void setDepartmentModerator(Long moderatorId, Long departmentId)
    {
        Optional<User> wrapperAdmin = userRepositories.findByIdPessimisticLockRead(moderatorId);
        wrapperAdmin.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND, String.format("Пользователь с id = %d не найден.", moderatorId)));
        Optional<Department> wrapperDepartment = departmentRepositories.findByIdWithPessimisticREAD(departmentId);
        wrapperDepartment.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND, String.format("Департамент с id = %d не найден.", departmentId)));

        User admin = wrapperAdmin.get();
        Department department = wrapperDepartment.get();


        if(!Objects.equals(admin.getModeratorBy(), wrapperDepartment.get()))
        {
            if(Objects.nonNull(admin.getModeratorBy()) )
                admin.getModeratorBy().setModerator(null);
            departmentRepositories.flush();
            wrapperDepartment.get().setModerator(wrapperAdmin.get());
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void setDepartmentBoss(Long bossId, Long departmentId)
    {
        Optional<User> wrapperBoss = userRepositories.findByIdPessimisticLockRead(bossId);
        wrapperBoss.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND, String.format("Пользователь с id = %d не найден.", wrapperBoss)));
        Optional<Department> wrapperDepartment = departmentRepositories.findByIdWithPessimisticREAD(departmentId);
        wrapperDepartment.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND, String.format("Департамент с id = %d не найден.", departmentId)));

        Department department = wrapperDepartment.get();
        User boss = wrapperBoss.get();

        if(!Objects.equals(boss.getBossBy(), department))
        {
            if(Objects.nonNull(boss.getBossBy()))
            {
                boss.getBossBy().setBoss(null);
            }
            departmentRepositories.flush();
            department.setBoss(boss);
            boss.setPosition(BOSS_POSITION);
        }
        setDepartmentModerator(bossId, departmentId);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public long count()
    {
        return departmentRepositories.count();
    }
}
