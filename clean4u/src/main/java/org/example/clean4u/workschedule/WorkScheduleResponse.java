package org.example.clean4u.workschedule;

import lombok.Data;

import java.time.LocalTime;

public class WorkScheduleResponse {

    @Data
    public static class ListDTO {
        private Long id;
        private Long employeeId;
        private String name;
        private LocalTime startTime;
        private LocalTime endTime;

        public ListDTO(WorkSchedule workSchedule) {
            this.id = workSchedule.getId();
            this.employeeId = workSchedule.getEmployee().getId();
            this.name = workSchedule.getEmployee().getName();
            this.startTime = workSchedule.getStartTime();
            this.endTime = workSchedule.getEndTime();
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private Long employeeId;
        private String name;
        private String username;
        private String email;
        private LocalTime startTime;
        private LocalTime endTime;

        public DetailDTO(WorkSchedule workSchedule) {
            this.id = workSchedule.getId();
            this.employeeId = workSchedule.getEmployee().getId();
            this.name = workSchedule.getEmployee().getName();
            this.username = workSchedule.getEmployee().getUsername();
            this.email = workSchedule.getEmployee().getEmail();
            this.startTime = workSchedule.getStartTime();
            this.endTime = workSchedule.getEndTime();
        }
    }

    @Data
    public static class SaveDTO {
        private Long id;
        private Long employeeId;
        private LocalTime startTime;
        private LocalTime endTime;

        public SaveDTO(WorkSchedule workSchedule) {
            this.id = workSchedule.getId();
            this.employeeId = workSchedule.getEmployee().getId();
            this.startTime = workSchedule.getStartTime();
        }
    }

    @Data
    public static class UpdateDTO {
        private Long id;
        private String name;
        private LocalTime startTime;
        private LocalTime endTime;

        public UpdateDTO(WorkSchedule workSchedule) {
            this.id = workSchedule.getId();
            this.name = workSchedule.getEmployee().getName();
            this.startTime = workSchedule.getStartTime();
            this.endTime = workSchedule.getEndTime();
        }
    }
}
