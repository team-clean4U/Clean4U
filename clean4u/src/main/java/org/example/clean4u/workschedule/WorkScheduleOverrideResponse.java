package org.example.clean4u.workschedule;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkScheduleOverrideResponse {
    @Data
    public static class ListDTO {
        private Long id;
        private String originalName;
        private String overrideName;
        private LocalDate date;

        public ListDTO(WorkScheduleOverride workScheduleOverride) {
            this.id = workScheduleOverride.getId();
            this.originalName = workScheduleOverride.getOriginalEmployee().getName();
            this.overrideName = workScheduleOverride.getOverrideEmployee().getName();
            this.date = workScheduleOverride.getDate();
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private String originalName;
        private String overrideName;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;

        public DetailDTO(WorkScheduleOverride workScheduleOverride) {
            this.id = workScheduleOverride.getId();
            this.originalName = workScheduleOverride.getOriginalEmployee().getName();
            this.overrideName = workScheduleOverride.getOverrideEmployee().getName();
            this.date = workScheduleOverride.getDate();
            this.startTime = workScheduleOverride.getStartTime();
            this.endTime = workScheduleOverride.getEndTime();
        }
    }

    @Data
    public static class SaveDTO {
        private Long id;
        private LocalDate date;
        private String OriginalName;
        private String OverrideName;
        private LocalTime startTime;
        private LocalTime endTime;

        public SaveDTO(WorkScheduleOverride workScheduleOverride) {
            this.id = workScheduleOverride.getId();
            this.date = workScheduleOverride.getDate();
            this.OriginalName = workScheduleOverride.getOriginalEmployee().getName();
            this.OverrideName = workScheduleOverride.getOverrideEmployee().getName();
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
