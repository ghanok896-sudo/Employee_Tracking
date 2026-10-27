
package com.employee.tracking.repository;

import com.employee.tracking.entity.Task;
import com.employee.tracking.entity.TaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findByAssignedEmployee_IdOrderByCreatedAtDesc(
            Long employeeId);

    List<Task> findByStatusOrderByCreatedAtDesc(
            TaskStatus status);

    List<Task> findByAssignedEmployee_IdAndStatusOrderByCreatedAtDesc(
            Long employeeId, TaskStatus status);

    boolean existsByIdAndAssignedEmployee_Id(
            Long taskId, Long employeeId);
    

long countByStatus(
        com.employee.tracking.entity.TaskStatus status);



}
