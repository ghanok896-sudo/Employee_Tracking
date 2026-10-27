
package com.employee.tracking.repository;

import com.employee.tracking.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployee_IdAndAttendanceDate(
            Long employeeId, LocalDate attendanceDate);

    List<Attendance> findByEmployee_IdOrderByAttendanceDateDesc(
            Long employeeId);

    List<Attendance> findByAttendanceDateOrderByCheckInDesc(
            LocalDate attendanceDate);

    List<Attendance> findAllByOrderByAttendanceDateDesc();
    
    List<Attendance> findByAttendanceDateBetweenOrderByAttendanceDateDesc(
            LocalDate startDate,
            LocalDate endDate
    );
}
