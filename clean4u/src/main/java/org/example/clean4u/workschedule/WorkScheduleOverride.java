package org.example.clean4u.workschedule;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.employee.Employee;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "work_schedule_override_tb")
public class WorkScheduleOverride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee originalEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee overrideEmployee;

    private LocalTime startTime;
    private LocalTime endTime;

    @Builder
    public WorkScheduleOverride(LocalDate date, Employee originalEmployee, Employee overrideEmployee, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.originalEmployee = originalEmployee;
        this.overrideEmployee = overrideEmployee;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void update(WorkScheduleOverrideRequest.UpdateDTO req, Employee originalEmployee, Employee overrideEmployee) {
        req.validate(originalEmployee, overrideEmployee);

        this.date = req.getDate();
        this.originalEmployee = originalEmployee;
        this.overrideEmployee = overrideEmployee;
        this.startTime = req.getStartTime();
        this.endTime = req.getEndTime();
    }
}
