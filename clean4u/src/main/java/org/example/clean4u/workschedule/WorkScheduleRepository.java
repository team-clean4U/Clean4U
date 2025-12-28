package org.example.clean4u.workschedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

//    Page<WorkSchedule> findAllSchedule(Pageable pageable);
}
