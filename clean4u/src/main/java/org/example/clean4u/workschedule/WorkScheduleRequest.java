package org.example.clean4u.workschedule;

import lombok.Data;
import org.example.clean4u.employee.Employee;

import java.time.LocalTime;
import java.util.List;

public class WorkScheduleRequest {
    @Data
    public static class SaveDTO {
        private LocalTime startTime;
        private LocalTime endTime;
        private Long employeeId;
        private List<DayOfWeek> days;

        public WorkSchedule toEntity(Employee employee) {
            return new WorkSchedule(startTime, endTime, employee, days);
        }
    }

    @Data
    public static class UpdateDTO {
        private LocalTime startTime;
        private LocalTime endTime;
        private Long employeeId;
        private List<DayOfWeek> days;

        // 검증 메서드
        public void validate(Employee employee) {
            if (startTime == null) {
                throw new IllegalArgumentException("시작 근무 시간은 필수 입력값 입니다,");
            }

            if (endTime == null) {
                throw new IllegalArgumentException("종료 근무 시간은 필수 입력값 입니다");
            }

            if (startTime.isAfter(endTime) || endTime.isBefore(startTime)) {
                throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야합니다");
            }

            if (employee == null) {
                throw new IllegalArgumentException("해당 직원을 찾을 수 없습니다.");
            }

            if (days == null || days.isEmpty()) {
                throw new IllegalArgumentException("근무 요일을 선택하세요");
            }
        }
    }
}
