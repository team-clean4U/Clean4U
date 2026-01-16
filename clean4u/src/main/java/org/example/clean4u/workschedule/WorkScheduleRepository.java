package org.example.clean4u.workschedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Optional;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    @Query("SELECT w FROM WorkSchedule w " +
            "WHERE w.employee.name LIKE concat('%', :keyword, '%') ")
    Page<WorkSchedule> findByNameContaining(@Param("keyword") String name, Pageable pageable);

    @Query("SELECT w FROM WorkSchedule w " +
            "WHERE w.employee.username LIKE concat('%', :keyword, '%') ")
    Page<WorkSchedule> findByUsernameContaining(@Param("keyword") String username, Pageable pageable);

    @Query("SELECT w FROM WorkSchedule w " +
            "WHERE w.startTime >= :start AND w.startTime <= :end")
    Page<WorkSchedule> searchByTimeRange(@Param("start") LocalTime start, @Param("end") LocalTime end, Pageable pageable);

    Page<WorkSchedule> findAll(Pageable pageable);

    Optional<WorkSchedule> findByEmployeeId(Long employeeId);

    @Query("SELECT w FROM WorkSchedule w " +
            "JOIN FETCH w.employee WHERE w.id = :id")
    Optional<WorkSchedule> findByIdWithEmployee(@Param("id") Long id);
}
