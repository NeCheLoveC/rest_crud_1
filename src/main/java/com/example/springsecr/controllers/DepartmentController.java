package com.example.springsecr.controllers;

import com.example.springsecr.dto.converter.DepartmentToDepartmentResponseDtoConverter;
import com.example.springsecr.dto.model.request.department.DepartmentAdminUpdateRequestDto;
import com.example.springsecr.dto.model.request.department.DepartmentBossUpdateRequestDto;
import com.example.springsecr.dto.model.request.department.DepartmentCreateRequestDTO;
import com.example.springsecr.dto.model.response.DepartmentResponseDto;
import com.example.springsecr.dto.model.request.department.DepartmentUpdateRequestDto;
import com.example.springsecr.exceptions.HttpCustomException;
import com.example.springsecr.models.Department;
import com.example.springsecr.services.DepartmentService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
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
    @Value("${host}")
    private String HOST;
    @Operation(summary = "Получение списка департаментов")
    @ApiResponses(value =
        @ApiResponse(responseCode = "200", description = "Список департаментов", content = @Content(mediaType = "application/json"))
    )
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>> findAll()
    {
        List<DepartmentResponseDto> list = departmentService.getAllActiveDepartments().stream().map(departmentToDepartmentDtoConverter).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
    @Operation(summary = "Создание нового департамента")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Департамент создан"),
                    @ApiResponse(responseCode = "400", description = "Недопустимые данные")
            }
    )
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody @Validated DepartmentCreateRequestDTO departmentDto, BindingResult bindingResult) throws URISyntaxException {
        if(bindingResult.hasErrors())
            throw new HttpCustomException(HttpStatus.BAD_REQUEST,bindingResult);

        Department department = departmentService.create(departmentDto);
        URI uri = new URI("http:" + HOST + "/departments/" + department.getId());
        return ResponseEntity.created(uri).build();
        //return ResponseEntity.ok(departmentToDepartmentDtoConverter.apply());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id ,@Valid @RequestBody DepartmentUpdateRequestDto departmentUpdateDto, BindingResult errors)
    {
        if(errors.hasErrors())
            throw new HttpCustomException(HttpStatus.BAD_REQUEST,errors);

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
