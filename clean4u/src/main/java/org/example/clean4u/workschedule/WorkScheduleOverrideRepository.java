package org.example.clean4u.workschedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;

public interface WorkScheduleOverrideRepository extends JpaRepository<WorkScheduleOverride, Long> {

    @Query("SELECT w FROM WorkScheduleOverride w " +
            "WHERE w.originalEmployee.name LIKE concat('%', :keyword, '%') ")
    Page<WorkScheduleOverride> findByOriginalEmployeeNameContaining(@Param("keyword") String name, Pageable pageable);

    @Query("SELECT w FROM WorkScheduleOverride w " +
            "WHERE w.overrideEmployee.name LIKE concat('%', :keyword, '%') ")
    Page<WorkScheduleOverride> findByOverrideEmployeeNameContaining(@Param("keyword") String name, Pageable pageable);

    @Query("SELECT w FROM WorkScheduleOverride w " +
            "WHERE w.startTime >= :start AND w.startTime <= :end")
    Page<WorkScheduleOverride> searchByTimeRange(@Param("start") LocalTime start, @Param("end") LocalTime end, Pageable pageable);

}
