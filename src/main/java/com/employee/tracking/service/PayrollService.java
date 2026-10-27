
package com.employee.tracking.service;

import com.employee.tracking.dto.PayrollRequest;
import com.employee.tracking.entity.Employee;
import com.employee.tracking.entity.Payroll;
import com.employee.tracking.entity.PayrollStatus;
import com.employee.tracking.repository.EmployeeRepository;
import com.employee.tracking.repository.PayrollRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    public PayrollService(
            PayrollRepository payrollRepository,
            EmployeeRepository employeeRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
    }

    public Payroll createPayroll(PayrollRequest request) {

        if (request.payrollMonth() == null
                || request.payrollMonth().getDayOfMonth() != 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payroll month must be the first day of the month");
        }

        if (request.payrollMonth().isAfter(
                LocalDate.now().withDayOfMonth(1))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payroll month cannot be in the future");
        }

        Employee employee = employeeRepository.findById(
                request.employeeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found"));

        if (!employee.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot create payroll for an inactive employee");
        }

        BigDecimal basic = request.basicSalary();
        BigDecimal allowances = request.allowances();
        BigDecimal deductions = request.deductions();

        if (basic == null || allowances == null || deductions == null
                || basic.signum() < 0
                || allowances.signum() < 0
                || deductions.signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Salary, allowances, and deductions must be non-negative");
        }

        BigDecimal grossSalary = basic.add(allowances);

        if (deductions.compareTo(grossSalary) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Deductions cannot exceed basic salary plus allowances");
        }

        if (payrollRepository.existsByEmployee_IdAndPayrollMonth(
                request.employeeId(), request.payrollMonth())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Payroll already exists for this employee and month");
        }

        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);
        payroll.setPayrollMonth(request.payrollMonth());
        payroll.setBasicSalary(basic);
        payroll.setAllowances(allowances);
        payroll.setDeductions(deductions);
        payroll.calculateNetSalary();
        payroll.setStatus(PayrollStatus.DRAFT);

        Payroll saved = payrollRepository.save(payroll);
        initializeEmployee(saved);
        return saved;
    }

    public List<Payroll> getAllPayrolls() {
        List<Payroll> payrolls =
                payrollRepository.findAllByOrderByPayrollMonthDesc();
        initializeEmployees(payrolls);
        return payrolls;
    }

    public Payroll getPayrollById(Long payrollId) {
        Payroll payroll = findPayroll(payrollId);
        initializeEmployee(payroll);
        return payroll;
    }

    public List<Payroll> getPayrollsByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Employee not found");
        }

        List<Payroll> payrolls =
                payrollRepository.findByEmployee_IdOrderByPayrollMonthDesc(
                        employeeId);
        initializeEmployees(payrolls);
        return payrolls;
    }

    public List<Payroll> getPayrollsByStatus(PayrollStatus status) {
        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Payroll status is required");
        }

        List<Payroll> payrolls =
                payrollRepository.findByStatusOrderByPayrollMonthDesc(status);
        initializeEmployees(payrolls);
        return payrolls;
    }

    public List<Payroll> getPayrollsBetweenMonths(
            LocalDate startMonth, LocalDate endMonth) {

        if (startMonth == null || endMonth == null
                || startMonth.getDayOfMonth() != 1
                || endMonth.getDayOfMonth() != 1
                || endMonth.isBefore(startMonth)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide valid first-of-month dates in chronological order");
        }

        List<Payroll> payrolls =
                payrollRepository
                        .findByPayrollMonthBetweenOrderByPayrollMonthDesc(
                                startMonth, endMonth);
        initializeEmployees(payrolls);
        return payrolls;
    }

    public Payroll updatePayrollStatus(
            Long payrollId, PayrollStatus newStatus) {

        Payroll payroll = findPayroll(payrollId);

        if (newStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Payroll status is required");
        }

        boolean validTransition = switch (payroll.getStatus()) {
            case DRAFT ->
                    newStatus == PayrollStatus.PROCESSED
                    || newStatus == PayrollStatus.CANCELLED;
            case PROCESSED ->
                    newStatus == PayrollStatus.PAID
                    || newStatus == PayrollStatus.CANCELLED;
            case PAID, CANCELLED -> false;
        };

        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Invalid payroll status transition from "
                            + payroll.getStatus() + " to " + newStatus);
        }

        payroll.setStatus(newStatus);

        Payroll saved = payrollRepository.save(payroll);
        initializeEmployee(saved);
        return saved;
    }

    private Payroll findPayroll(Long payrollId) {
        return payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Payroll record not found"));
    }

    private void initializeEmployees(List<Payroll> payrolls) {
        payrolls.forEach(this::initializeEmployee);
    }

    private void initializeEmployee(Payroll payroll) {
        payroll.getEmployee().getFullName();
    }
}
