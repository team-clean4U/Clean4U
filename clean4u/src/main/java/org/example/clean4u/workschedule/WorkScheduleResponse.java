package org.example.clean4u.workschedule;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

public class WorkScheduleResponse {

    @Data
    public static class ListDTO {
        private Long employeeId;
        private LocalTime startTime;
        private LocalTime endTime;
        private List<DayOfWeek> days;

        public ListDTO(WorkSchedule workSchedule) {
            this.employeeId = workSchedule.getEmployee().getId();
            this.startTime = workSchedule.getStartTime();
            this.endTime = workSchedule.getEndTime();
            this.days = workSchedule.getDays();
        }
    }

    @Data
    public static class SaveDTO {
        private Long id;
        private Long employeeId;
        private LocalTime startTime;
        private LocalTime endTime;
        private List<DayOfWeek> days;

        public SaveDTO(WorkSchedule workSchedule) {
            this.id = workSchedule.getId();
            this.employeeId = workSchedule.getEmployee().getId();
            this.startTime = workSchedule.getStartTime();
            this.endTime = workSchedule.getEndTime();
            this.days = workSchedule.getDays();
        }
    }

    @Data
    public static class UpdateDTO {
        private Long employeeId;
        private LocalTime startTime;
        private LocalTime endTime;
        private List<DayOfWeek> days;

        public UpdateDTO(WorkSchedule workSchedule) {
            this.employeeId = workSchedule.getEmployee().getId();
            this.startTime = workSchedule.getStartTime();
            this.endTime = workSchedule.getEndTime();
            this.days = workSchedule.getDays();
        }
    }
}
