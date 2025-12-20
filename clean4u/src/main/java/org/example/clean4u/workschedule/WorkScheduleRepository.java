package org.example.clean4u.workschedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    List<WorkSchedule> findByEmployeeName(String employeeName);

    List<WorkSchedule> findByStartTimeBetween(LocalTime start, LocalTime end);
}
