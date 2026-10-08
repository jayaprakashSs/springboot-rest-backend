package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeRequest;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.BadRequestException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public Employee createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Employee with email '" + request.getEmail() + "' already exists");
        }

        Employee employee = Employee.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone().trim())
                .department(request.getDepartment().trim())
                .salary(request.getSalary())
                .joiningDate(request.getJoiningDate())
                .status(request.getStatus())
                .build();

        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    @Transactional
    public Employee updateEmployee(Long id, EmployeeRequest request) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // If email changed, ensure new email is not taken by another employee
        employeeRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .ifPresent(other -> {
                    if (!other.getId().equals(id)) {
                        throw new BadRequestException("Employee with email '" + request.getEmail() + "' already exists");
                    }
                });

        existingEmployee.setName(request.getName().trim());
        existingEmployee.setEmail(request.getEmail().trim().toLowerCase());
        existingEmployee.setPhone(request.getPhone().trim());
        existingEmployee.setDepartment(request.getDepartment().trim());
        existingEmployee.setSalary(request.getSalary());
        existingEmployee.setJoiningDate(request.getJoiningDate());
        existingEmployee.setStatus(request.getStatus());

        return employeeRepository.save(existingEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        employeeRepository.delete(existingEmployee);
    }
}
