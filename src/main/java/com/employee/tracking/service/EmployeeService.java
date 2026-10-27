
package com.employee.tracking.service;

import com.employee.tracking.dto.EmployeeRequest;
import com.employee.tracking.entity.Employee;
import com.employee.tracking.repository.EmployeeRepository;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee create(EmployeeRequest request) {
        if (employeeRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Employee email already exists");
        }

        Employee employee = new Employee();
        copyRequest(request, employee);
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll(
                Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found"));
    }

    public Employee update(Long id, EmployeeRequest request) {
        Employee employee = findById(id);

        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(
                request.email(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Employee email already exists");
        }

        copyRequest(request, employee);
        return employeeRepository.save(employee);
    }

    public void delete(Long id) {
        Employee employee = findById(id);
        employeeRepository.delete(employee);
    }

    @Transactional(readOnly = true)
    public List<Employee> search(
            String name, String department, Boolean active) {

        List<Employee> employees = employeeRepository.findAll(
                Sort.by(Sort.Direction.ASC, "id"));

        return employees.stream()
                .filter(e -> name == null || name.isBlank()
                        || e.getFullName().toLowerCase()
                        .contains(name.trim().toLowerCase()))
                .filter(e -> department == null || department.isBlank()
                        || (e.getDepartment() != null
                        && e.getDepartment().equalsIgnoreCase(
                                department.trim())))
                .filter(e -> active == null || e.isActive() == active)
                .toList();
    }

    private void copyRequest(
            EmployeeRequest request, Employee employee) {

        employee.setFullName(request.fullName().trim());
        employee.setEmail(request.email().trim().toLowerCase());
        employee.setPhone(request.phone());
        employee.setDepartment(request.department());
        employee.setDesignation(request.designation());
        employee.setJoiningDate(request.joiningDate());

        if (request.active() != null) {
            employee.setActive(request.active());
        }
    }
}
