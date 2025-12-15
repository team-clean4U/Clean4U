package org.example.clean4u.workschedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkScheduleOverrideRepository extends JpaRepository<WorkScheduleOverride, Long> {
    List<WorkScheduleOverride> findByDate(LocalDate date);

    List<WorkScheduleOverride> findByOriginalEmployeeId(Long employeeId);

    List<WorkScheduleOverride> findByOverrideEmployeeId(Long employeeId);
}
