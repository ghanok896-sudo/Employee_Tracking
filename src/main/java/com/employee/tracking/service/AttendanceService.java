
package com.employee.tracking.service;

import com.employee.tracking.dto.AttendanceRequest;
import com.employee.tracking.entity.Attendance;
import com.employee.tracking.entity.Employee;
import com.employee.tracking.repository.AttendanceRepository;
import com.employee.tracking.repository.EmployeeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    public Attendance checkIn(Long employeeId, AttendanceRequest request) {
        Employee employee = findActiveEmployee(employeeId);
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByEmployee_IdAndAttendanceDate(employeeId, today)
                .orElse(null);

        if (attendance != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Employee has already checked in today");
        }

        attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(today);
        attendance.setCheckIn(LocalDateTime.now());

        if (request != null) {
            attendance.setNotes(request.notes());
        }

        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(Long employeeId) {
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByEmployee_IdAndAttendanceDate(employeeId, today)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No check-in record found for today"));

        if (attendance.getCheckOut() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Employee has already checked out today");
        }

        LocalDateTime checkOutTime = LocalDateTime.now();

        if (checkOutTime.isBefore(attendance.getCheckIn())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-out cannot be before check-in");
        }

        attendance.setCheckOut(checkOutTime);
        attendance.calculateWorkingMinutes();

        return attendanceRepository.save(attendance);
    }


@Transactional(readOnly = true)
public List<Attendance> getMyHistory(Long employeeId) {
    List<Attendance> records = attendanceRepository
            .findByEmployee_IdOrderByAttendanceDateDesc(employeeId);

    // Initialize the lazy employee relationship inside the transaction.
    records.forEach(record -> record.getEmployee().getFullName());

    return records;
}

@Transactional(readOnly = true)
public List<Attendance> getAllAttendance() {
    List<Attendance> records =
            attendanceRepository.findAllByOrderByAttendanceDateDesc();

    records.forEach(record -> record.getEmployee().getFullName());

    return records;
}

@Transactional(readOnly = true)
public List<Attendance> getAttendanceByDate(LocalDate date) {
    List<Attendance> records =
            attendanceRepository.findByAttendanceDateOrderByCheckInDesc(date);

    records.forEach(record -> record.getEmployee().getFullName());

    return records;
}


    private Employee findActiveEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee profile not found"));

        if (!employee.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Inactive employees cannot record attendance");
        }

        return employee;
    }
}

