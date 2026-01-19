package org.example.clean4u.workschedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.clean4u.employee.Employee;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkScheduleRequest {
    @Data
    public static class SaveDTO {
        @NotNull(message = "근무자의 ID는 비워둘 수 없습니다.")
        private Long employeeId;
        @NotBlank(message = "근무자의 이름은 비워둘 수 없습니다.")
        private String name;
        @NotNull(message = "사유는 반드시 선택해야 합니다.")
        private ScheduleReason reason;

        private LocalTime startTime;
        private LocalTime endTime;

        private LocalDate date;
        private Long overrideId;
        private LocalTime overrideStartTime;
        private LocalTime overrideEndTime;

        public boolean isSick() {
            return reason == ScheduleReason.SICK;
        }

        public WorkSchedule toNormalEntity(Employee employee) {
            return new WorkSchedule(employee, startTime, endTime);
        }

        public WorkScheduleOverride toSickEntity(Employee originalEmployee, Employee overrideEmployee) {
            LocalTime start = overrideStartTime != null ? overrideStartTime : LocalTime.of(9, 0);
            LocalTime end = overrideEndTime != null ? overrideEndTime : LocalTime.of(18, 0);
            return new WorkScheduleOverride(date, originalEmployee, overrideEmployee, start, end);
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "근무자의 이름은 비워둘 수 없습니다.")
        private String name;
        @NotNull(message = "시작 시간은 비워둘 수 없습니다.")
        private String startTime;
        @NotNull(message = "종료 시간은 비워둘 수 없습니다.")
        private String endTime;
    }
}
