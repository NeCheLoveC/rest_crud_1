package com.example.springsecr.services;

import com.example.springsecr.dto.model.request.department.DepartmentCreateRequestDTO;
import com.example.springsecr.dto.model.request.department.DepartmentUpdateRequestDto;
import com.example.springsecr.exceptions.HttpCustomException;
import com.example.springsecr.models.Department;
import com.example.springsecr.models.Role;
import com.example.springsecr.models.User;
import com.example.springsecr.repositories.DepartmentRepositories;
import com.example.springsecr.repositories.UserRepositories;
import com.example.springsecr.validators.DepartmentCreateDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentService
{
    private final DepartmentRepositories departmentRepositories;
    private final UserRepositories userRepositories;
    public static final String BOSS_POSITION = "Руководитель департамента";

    private final DepartmentCreateDtoValidator departmentCreateDtoValidator;
    @Transactional()
    public Department create(DepartmentCreateRequestDTO departmentDto)
    {
        BindingResult bindingResult = new DirectFieldBindingResult(departmentDto, "departmentDto");
        departmentCreateDtoValidator.validate(departmentDto, bindingResult);
        if(bindingResult.hasErrors())
        {
            //throw new BadRequestException(bindingResult.getAllErrors().stream().map((err) -> err.getDefaultMessage()).collect(Collectors.joining()));
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, bindingResult);
        }
        Department department = new Department();
        department.setName(departmentDto.getName());
        if(departmentDto.getDepartmentParentId() != null)
        {
            Optional<Department> parentDepartment = departmentRepositories.findById(departmentDto.getDepartmentParentId());
            parentDepartment.orElseThrow(() -> new HttpCustomException(HttpStatus.BAD_REQUEST, "departmentParentId не найден"));
            department.setDepartmentParent(parentDepartment.get());
        }
        if(departmentDto.getBossId() != null)
        {
            Optional<User> bossWrapper = userRepositories.findByIdPessimisticLockRead(departmentDto.getBossId());
            bossWrapper.orElseThrow(() -> new HttpCustomException(HttpStatus.BAD_REQUEST, String.format("Пользователь с id = %d не найден", departmentDto.getBossId())));
            User boss = bossWrapper.get();


            department.setBoss(boss);
            if(departmentDto.getModeratorId() == null)
                department.setModerator(boss);
        }
        if(departmentDto.getModeratorId() != null)
        {
            Optional<User> moderatorWrapper = userRepositories.findByIdPessimisticLockRead(departmentDto.getModeratorId());
            moderatorWrapper.orElseThrow(() -> new HttpCustomException(HttpStatus.BAD_REQUEST, String.format("Пользователь с id = %d не найден", departmentDto.getModeratorId())));
            department.setModerator(moderatorWrapper.get());
            //moderatorWrapper.get().setDepartment(department);
        }
        return departmentRepositories.save(department);
    }

    public Collection<Department> getAllActiveDepartments()
    {
        return departmentRepositories.getAllActiveDepartments();
    }

    public Department update(DepartmentUpdateRequestDto departmentUpdateDto)
    {
        Optional<Department> department = departmentRepositories.findByIdWithPessimisticWRITE(departmentUpdateDto.getId());
        department.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND));
        if(department.get().isDeleted())
            throw new HttpCustomException(HttpStatus.NOT_FOUND);
        department.get().setName(departmentUpdateDto.getName().trim());
        return department.get();
    }

    // TODO: 20.11.2023  Полностью удаляет сущность из БД
    public void delete(Long id)
    {
        Optional<Department> wrapperDepartment = departmentRepositories.findByIdWithPessimisticWRITE(id);
        wrapperDepartment.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND));
        Department department = wrapperDepartment.get();
        //Если удаляемый узел имел листья - установить у всех дочерних объектов нового родителя - родитель удаляемого департмаента
        for(Department d : department.getDepartments())
            d.setDepartmentParent(department.getDepartmentParent());
        departmentRepositories.delete(department);
    }


    @Transactional
    public void setDepartmentModerator(Long moderatorId, Long departmentId)
    {
        Optional<User> wrapperAdmin = userRepositories.findById(moderatorId);
        wrapperAdmin.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND, String.format("Пользователь с id = %d не найден.", moderatorId)));
        Optional<Department> wrapperDepartment = departmentRepositories.findByIdWithPessimisticWRITE(departmentId);
        wrapperDepartment.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND, String.format("Департамент с id = %d не найден.", departmentId)));

        User admin = wrapperAdmin.get();
        Department department = wrapperDepartment.get();

        if(admin.getRole().equals(RoleService.getADMIN_ROLE()))
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Нельзя менять департамент у Администратора");

        if(department.getDepartmentParent() == null)
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Главный департамент - неизменяемая сущность");
        if(!Objects.equals(admin.getModeratorBy(), department))
        {
            //todo Можно убрать IF, но будет ли ошибка?
            if(Objects.nonNull(admin.getModeratorBy()))
                admin.getModeratorBy().setModerator(null);
            //admin.getModeratorBy().setModerator(null);
            departmentRepositories.flush();
            department.setModerator(wrapperAdmin.get());
        }
    }

    @Transactional
    public void setDepartmentBoss(Long bossId, Long departmentId)
    {
        Optional<User> wrapperBoss = userRepositories.findByIdPessimisticLockRead(bossId);
        wrapperBoss.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND, String.format("Пользователь с id = %d не найден.", bossId)));
        Optional<Department> wrapperDepartment = departmentRepositories.findByIdWithPessimisticREAD(departmentId);
        wrapperDepartment.orElseThrow(() -> new HttpCustomException(HttpStatus.NOT_FOUND, String.format("Департамент с id = %d не найден.", departmentId)));

        Department department = wrapperDepartment.get();
        User boss = wrapperBoss.get();
        if(boss.getRole().equals(RoleService.getADMIN_ROLE()))
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Нельзя менять департамент у Администратора");
        if(department.getDepartmentParent() == null)
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Главный департамент - неизменяемая сущность");
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
    @Transactional
    public long count()
    {
        return departmentRepositories.getCountEntities();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void inactive(Long id)
    {
        Optional<Department> departmentWrapper = departmentRepositories.findById(id);
        departmentWrapper.orElseThrow(() -> new HttpCustomException(HttpStatus.BAD_REQUEST, String.format("Департамент с id = %d не найден", id)));
        Department department = departmentWrapper.get();
        //Нельзя пометить "уделнный" главный департамент!
        if(department.getDepartmentParent() == null)
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Нельзя пометить как \'удаленный\' главный (корневой) департамент");
        department.setDeleted(true);
        department.getDepartments().stream().forEach(d -> d.setDepartmentParent(department.getDepartmentParent()));

        Collection<User> employers = userRepositories.getEmployersByDepartmentId(id);

        employers.stream().forEach(emp ->
        {
            emp.setPosition("");
            emp.setDepartment(department.getDepartmentParent());
        });
        //department.setBoss(null);
        //department.setModerator(null);
        setDepartmentBoss(null, department.getId());
        setDepartmentModerator(null, department.getId());
    }

    @Transactional
    public Optional<Department> find(long id)
    {
        return departmentRepositories.findById(id);
    }

    public Department getRootDepartment()
    {
        return departmentRepositories.findRootDepartment();
    }
}
