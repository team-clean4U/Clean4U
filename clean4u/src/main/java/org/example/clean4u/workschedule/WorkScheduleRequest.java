package org.example.clean4u.workschedule;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.clean4u.employee.Employee;

import java.time.LocalTime;
import java.util.List;

public class WorkScheduleRequest {
    @Data
    public static class SaveDTO {
        @NotNull(message = "근무자의 ID는 비워둘 수 없습니다.")
        private Long employeeId;
        @NotNull(message = "시작 시간은 비워둘 수 없습니다.")
        private LocalTime startTime;
        @NotNull(message = "종료 시간은 비워둘 수 없습니다.")
        private LocalTime endTime;
        @NotNull(message = "근무스케줄은 비워둘 수 없습니다.")
        private List<DayOfWeek> days;

        public WorkSchedule toEntity(Employee employee) {
            return new WorkSchedule(employee, startTime, endTime, days);
        }
    }

    @Data
    public static class UpdateDTO {
        @NotNull(message = "근무자의 ID는 비워둘 수 없습니다.")
        private Long employeeId;
        @NotNull(message = "시작 시간은 비워둘 수 없습니다.")
        private LocalTime startTime;
        @NotNull(message = "종료 시간은 비워둘 수 없습니다.")
        private LocalTime endTime;
        @NotNull(message = "근무스케줄은 비워둘 수 없습니다.")
        private List<DayOfWeek> days;
    }
}
