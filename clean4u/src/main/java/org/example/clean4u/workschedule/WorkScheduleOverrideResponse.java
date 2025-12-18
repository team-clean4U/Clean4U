package org.example.clean4u.workschedule;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkScheduleOverrideResponse {

    @Data
    public static class SaveDTO {
        private LocalDate date;
        private Long originalEmployeeId;
        private Long overrideEmployeeId;
        private LocalTime startTime;
        private LocalTime endTime;

        public SaveDTO(WorkScheduleOverride workScheduleOverride) {
            this.date = workScheduleOverride.getDate();
            this.originalEmployeeId = workScheduleOverride.getOriginalEmployee().getId();
            this.overrideEmployeeId = workScheduleOverride.getOverrideEmployee().getId();
            this.startTime = workScheduleOverride.getStartTime();
            this.endTime = workScheduleOverride.getEndTime();
        }
    }

    @Data
    public static class UpdateDTO {
        private LocalDate date;
        private Long originalEmployeeId;
        private Long overrideEmployeeId;
        private LocalTime startTime;
        private LocalTime endTime;

        public UpdateDTO(WorkScheduleOverride workScheduleOverride) {
            this.date = workScheduleOverride.getDate();
            this.originalEmployeeId = workScheduleOverride.getOriginalEmployee().getId();
            this.overrideEmployeeId = workScheduleOverride.getOverrideEmployee().getId();
            this.startTime = workScheduleOverride.getStartTime();
            this.endTime = workScheduleOverride.getEndTime();
        }
    }
}
