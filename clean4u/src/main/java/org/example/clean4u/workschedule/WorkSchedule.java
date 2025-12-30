package org.example.clean4u.workschedule;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "work_schedule_tb")
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Employee employee;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Builder
    public WorkSchedule (Employee employee, LocalTime startTime, LocalTime endTime) {
        this.employee = employee;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void update(WorkScheduleRequest.UpdateDTO req, Employee employee) {
        this.startTime = req.getStartTime();
        this.endTime = req.getEndTime();
        this.employee = employee;
    }
}
