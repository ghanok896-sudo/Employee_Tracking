
package com.employee.tracking.repository;

import com.employee.tracking.entity.Payroll;
import com.employee.tracking.entity.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findAllByOrderByPayrollMonthDesc();

    List<Payroll> findByEmployee_IdOrderByPayrollMonthDesc(
            Long employeeId);

    List<Payroll> findByStatusOrderByPayrollMonthDesc(
            PayrollStatus status);

    Optional<Payroll> findByEmployee_IdAndPayrollMonth(
            Long employeeId,
            LocalDate payrollMonth);

    boolean existsByEmployee_IdAndPayrollMonth(
            Long employeeId,
            LocalDate payrollMonth);

    List<Payroll> findByPayrollMonthBetweenOrderByPayrollMonthDesc(
            LocalDate startMonth,
            LocalDate endMonth);
    

long countByStatus(
        com.employee.tracking.entity.PayrollStatus status);

List<Payroll> findByPayrollMonthOrderByEmployee_IdAsc(
        LocalDate payrollMonth
);

}

