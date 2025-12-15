package org.example.clean4u.workschedule;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.clean4u.employee.Employee;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkScheduleOverrideRequest {
    @Data
    public static class SaveDTO {
        @NotNull(message = "날짜는 비워둘 수 없습니다.")
        private LocalDate date;
        @NotNull(message = "기존 근무자의 ID는 비워둘 수 없습니다.")
        private Long originalEmployeeId;
        @NotNull(message = "교체 근무자의 ID는 비워둘 수 없습니다.")
        private Long overrideEmployeeId;
        @NotNull(message = "시작 시간은 비워둘 수 없습니다.")
        private LocalTime startTime;
        @NotNull(message = "종료 시간은 비워둘 수 없습니다.")
        private LocalTime endTime;

        public WorkScheduleOverride toEntity(Employee originalEmployee, Employee overrideEmployee) {
            return new WorkScheduleOverride(date, originalEmployee, overrideEmployee, startTime, endTime);
        }
    }

    @Data
    public static class UpdateDTO {
        @NotNull(message = "날짜는 비워둘 수 없습니다.")
        private LocalDate date;
        @NotNull(message = "기존 근무자의 ID는 비워둘 수 없습니다.")
        private Long originalEmployeeId;
        @NotNull(message = "교체 근무자의 ID는 비워둘 수 없습니다.")
        private Long overrideEmployeeId;
        @NotNull(message = "시작 시간은 비워둘 수 없습니다.")
        private LocalTime startTime;
        @NotNull(message = "종료 시간은 비워둘 수 없습니다.")
        private LocalTime endTime;
    }
}
