package org.example.clean4u.employee;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
