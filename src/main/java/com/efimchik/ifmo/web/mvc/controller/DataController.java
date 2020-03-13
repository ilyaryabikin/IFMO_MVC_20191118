package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.entity.DepartmentEntity;
import com.efimchik.ifmo.web.mvc.entity.EmployeeEntity;
import com.efimchik.ifmo.web.mvc.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/employees")
public class DataController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public List<Employee> getEmployees(@RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size,
                                       @RequestParam(required = false) String sort) {
        Pageable pageable = getPageable(page, size, sort);
        Page<EmployeeEntity> entities = employeeRepository.findAll(pageable);
        List<Employee> employees = new ArrayList<>();
        entities.forEach(entity -> employees.add(entityToDomainChain(entity, false)));
        return employees;
    }

    @GetMapping(value = "/{id}")
    public Employee getEmployee(@PathVariable Long id,
                                @RequestParam(name = "full_chain", required = false) boolean fullChain) {
        EmployeeEntity entity = employeeRepository.findById(id).get();
        return entityToDomainChain(entity, fullChain);
    }

    @GetMapping(value = "/by_manager/{managerId}")
    public List<Employee> getEmployeesByManager(@PathVariable Long managerId,
                                                @RequestParam(required = false) Integer page,
                                                @RequestParam(required = false) Integer size,
                                                @RequestParam(required = false) String sort) {
        Pageable pageable = getPageable(page, size, sort);
        List<EmployeeEntity> entities = employeeRepository.findAllByManagerId(managerId, pageable);
        List<Employee> employees = new ArrayList<>();
        entities.forEach(entity -> employees.add(entityToDomainChain(entity, false)));
        return employees;
    }

    @GetMapping(value = "/by_department/{departmentIdOrName}")
    public List<Employee> getEmployeesByDepartment(@PathVariable String departmentIdOrName,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size,
                                                   @RequestParam(required = false) String sort) {
        Pageable pageable = getPageable(page, size, sort);
        List<EmployeeEntity> entities;
        if (departmentIdOrName.matches("[0-9]+")) {
            entities = employeeRepository.findAllByDepartmentId(Long.parseLong(departmentIdOrName), pageable);
        } else {
            entities = employeeRepository.findAllByDepartmentName(departmentIdOrName, pageable);
        }
        List<Employee> employees = new ArrayList<>();
        entities.forEach(entity -> employees.add(entityToDomainChain(entity, false)));
        return employees;
    }

    private Pageable getPageable(Integer page, Integer size, String sort) {
        if (page == null || size == null) {
            return Pageable.unpaged();
        }
        if (sort == null) {
            return PageRequest.of(page, size);
        }
        return PageRequest.of(page, size, Sort.by(sort).ascending());
    }

    private Department entityToDomain(DepartmentEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Department(entity.getId(),
                entity.getName(),
                entity.getLocation());
    }

    private Employee entityToDomain(EmployeeEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Employee(entity.getId(),
                new FullName(entity.getFirstName(), entity.getLastName(), entity.getMiddleName()),
                entity.getPosition(),
                entity.getHired(),
                entity.getSalary(),
                null,
                entityToDomain(entity.getDepartment()));
    }

    private Employee entityToDomainChain(EmployeeEntity entity, boolean fullChain) {
        if (entity == null) {
            return null;
        }
        Employee manager = fullChain ?
                entityToDomainChain(entity.getManager(), true) : entityToDomain(entity.getManager());
        return new Employee(entity.getId(),
                new FullName(entity.getFirstName(), entity.getLastName(), entity.getMiddleName()),
                entity.getPosition(),
                entity.getHired(),
                entity.getSalary(),
                manager,
                entityToDomain(entity.getDepartment()));
    }
}
