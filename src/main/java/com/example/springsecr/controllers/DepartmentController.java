package com.example.springsecr.controllers;

import com.example.springsecr.dto.converter.DepartmentToDepartmentResponseDtoConverter;
import com.example.springsecr.dto.model.request.department.DepartmentAdminUpdateRequestDto;
import com.example.springsecr.dto.model.request.department.DepartmentBossUpdateRequestDto;
import com.example.springsecr.dto.model.request.department.DepartmentCreateRequestDTO;
import com.example.springsecr.dto.model.response.DepartmentResponseDto;
import com.example.springsecr.dto.model.request.department.DepartmentUpdateRequestDto;
import com.example.springsecr.exceptions.HttpCustomException;
import com.example.springsecr.services.DepartmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
@Validated
public class DepartmentController
{
    private final DepartmentToDepartmentResponseDtoConverter departmentToDepartmentDtoConverter;
    private final DepartmentService departmentService;
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>> findAll()
    {
        List<DepartmentResponseDto> list = departmentService.getAllActiveDepartments().stream().map(departmentToDepartmentDtoConverter).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Validated DepartmentCreateRequestDTO departmentDto, BindingResult bindingResult)
    {
        if(bindingResult.hasErrors())
            throw new HttpCustomException(HttpStatus.BAD_REQUEST,bindingResult);
        return ResponseEntity.ok(departmentToDepartmentDtoConverter.apply(departmentService.create(departmentDto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id , @RequestBody DepartmentUpdateRequestDto departmentUpdateDto)
    {
        departmentUpdateDto.setId(id);
        departmentService.update(departmentUpdateDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id)
    {
        departmentService.inactive(id);
        //departmentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{department_id}/admin")
    public ResponseEntity<?> setDepartmentAdmin(@RequestBody @Valid DepartmentAdminUpdateRequestDto updateDto, BindingResult bindingResult,
                                                @PathVariable("department_id") @Min(0) Long departmentId
                                                )
    {
        if(bindingResult.hasErrors())
            throw new HttpCustomException(HttpStatus.BAD_REQUEST);
        departmentService.setDepartmentModerator(updateDto.getModeratorId(), departmentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{department_id}/boss")
    public ResponseEntity<?> setDepartmentBoss(@RequestBody @Valid DepartmentBossUpdateRequestDto updateDto, BindingResult bindingResult,
                                               @PathVariable("department_id") @Min(0) Long departmentId
                                               )
    {
        if(bindingResult.hasErrors())
            throw new HttpCustomException(HttpStatus.BAD_REQUEST);
        departmentService.setDepartmentBoss(updateDto.getBossId(),departmentId);
        return ResponseEntity.ok().build();
    }
}
