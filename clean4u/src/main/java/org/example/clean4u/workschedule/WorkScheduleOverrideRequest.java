package org.example.clean4u.workschedule;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkScheduleOverrideRequest {
    @Data
    public static class UpdateDTO {
        @NotNull(message = "날짜는 비워둘 수 없습니다.")
        private LocalDate date;
        @NotNull(message = "기존 근무자의 ID는 비워둘 수 없습니다.")
        private Long originalId;
        @NotNull(message = "교체 근무자의 ID는 비워둘 수 없습니다.")
        private Long overrideId;
        @NotNull(message = "시작 시간은 비워둘 수 없습니다.")
        private LocalTime startTime;
        @NotNull(message = "종료 시간은 비워둘 수 없습니다.")
        private LocalTime endTime;
    }
}
