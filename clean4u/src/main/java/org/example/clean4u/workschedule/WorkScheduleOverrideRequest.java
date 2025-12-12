package org.example.clean4u.workschedule;

import lombok.Data;
import org.example.clean4u.employee.Employee;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkScheduleOverrideRequest {
    @Data
    public static class SaveDTO {
        private LocalDate date;
        private Long originalEmployeeId;
        private Long overrideEmployeeId;
        private LocalTime startTime;
        private LocalTime endTime;

        public WorkScheduleOverride toEntity(Employee originalEmployee, Employee overrideEmployee) {
            return new WorkScheduleOverride(date, originalEmployee, overrideEmployee, startTime, endTime);
        }
    }

    @Data
    public static class UpdateDTO {
        private LocalDate date;
        private Long originalEmployeeId;
        private Long overrideEmployeeId;
        private LocalTime startTime;
        private LocalTime endTime;

        // 검증 메서드
        public void validate(Employee originalEmployee, Employee overrideEmployee) {
            if (startTime == null) {
                throw new IllegalArgumentException("시작 근무 시간은 필수 입력값 입니다,");
            }

            if (endTime == null) {
                throw new IllegalArgumentException("종료 근무 시간은 필수 입력값 입니다");
            }

            if (startTime.isAfter(endTime) || endTime.isBefore(startTime)) {
                throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야합니다");
            }

            if (originalEmployee == null) {
                throw new IllegalArgumentException("해당 직원을 찾을 수 없습니다.");
            }

            if (overrideEmployee == null) {
                throw new IllegalArgumentException("해당 직원을 찾을 수 없습니다.");
            }
        }
    }
}
