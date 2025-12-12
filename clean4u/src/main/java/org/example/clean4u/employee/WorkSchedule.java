package org.example.clean4u.employee;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "work_schedule_day_tb", joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "day")
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> days;

    private LocalTime startTime;
    private LocalTime endTime;
}
