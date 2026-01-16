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
            this.endTime = workSchedule.getEndTime();
        }
    }

    @Data
    public static class UpdateDTO {
        private Long id;
        private String name;
        private String startTime;
        private String endTime;

        public UpdateDTO(WorkSchedule workSchedule) {
            this.id = workSchedule.getId();
            this.name = workSchedule.getEmployee().getName();
            this.startTime = workSchedule.getStartTime().toString();
            this.endTime = workSchedule.getEndTime().toString();
        }
    }

    @Data
    public static class DataDTO {
        private Long id;
        private LocalTime startTime;
        private LocalTime endTime;
        private Boolean working;
        private String message;

        public DataDTO(WorkSchedule workSchedule) {
            if (workSchedule == null) {
                this.working = false;
                this.message = "스케줄이 등록되어 있지 않습니다.";
            } else {
                this.id = workSchedule.getId();
                this.startTime = workSchedule.getStartTime();
                this.endTime = workSchedule.getEndTime();
                this.working = true;
            }
        }
    }
}
