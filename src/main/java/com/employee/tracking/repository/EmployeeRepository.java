
package com.employee.tracking.repository;

import com.employee.tracking.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Employee> findByFullNameContainingIgnoreCase(String fullName);

    List<Employee> findByDepartmentIgnoreCase(String department);

    List<Employee> findByActive(boolean active);
    

     long countByActiveTrue();

     long countByActiveFalse();

}
