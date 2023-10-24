package com.example.springsecr.controllers;

import com.example.springsecr.dto.converter.DepartmentToDepartmentDtoConverter;
import com.example.springsecr.dto.model.DepartmentCreateRequestDTO;
import com.example.springsecr.dto.model.DepartmentResponseDto;
import com.example.springsecr.exceptions.BadRequestException;
import com.example.springsecr.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController
{
    private final DepartmentToDepartmentDtoConverter departmentToDepartmentDtoConverter;
    private final DepartmentService departmentService;
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>> findAll()
    {
        List<DepartmentResponseDto> list = departmentService.getAll().stream().map(departmentToDepartmentDtoConverter).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Validated DepartmentCreateRequestDTO departmentDto, BindingResult bindingResult)
    {
        if(bindingResult.hasErrors())
            throw new BadRequestException(bindingResult.getAllErrors().stream().map(t -> t.getDefaultMessage()).collect(Collectors.joining()));
        return ResponseEntity.ok(departmentToDepartmentDtoConverter.apply(departmentService.create(departmentDto)));
    }
}
