package org.example.clean4u.workschedule;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.time.BaseTimeEntity;

import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "work_schedule_tb")
public class WorkSchedule extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Employee employee;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "work_schedule_day_tb", joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "day")
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> days;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Builder
    public WorkSchedule (Employee employee, LocalTime startTime, LocalTime endTime, List<DayOfWeek> days) {
        this.employee = employee;
        this.startTime = startTime;
        this.endTime = endTime;
        this.days = days;
    }

    public void update(WorkScheduleRequest.UpdateDTO req, Employee employee) {
        this.startTime = req.getStartTime();
        this.endTime = req.getEndTime();
        this.employee = employee;
        this.days = req.getDays();
    }
}
