package org.example.clean4u.workschedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class WorkScheduleApiController {

    private final WorkScheduleService workScheduleService;

    @PutMapping("/schedules/{scheduleId}/edit")
    public ResponseEntity<ApiResponse<WorkScheduleResponse.UpdateDTO>> scheduleUpdateProc(
            @PathVariable Long scheduleId,
            @RequestBody @Valid WorkScheduleRequest.UpdateDTO updateDTO
    ) {
        WorkSchedule schedule = workScheduleService.scheduleUpdateProc(scheduleId, updateDTO);
        WorkScheduleResponse.UpdateDTO responseDTO = new WorkScheduleResponse.UpdateDTO(schedule);

        return ResponseEntity.ok(ApiResponse.ok(responseDTO));
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long scheduleId
    ) {
        workScheduleService.delete(scheduleId);

        return ResponseEntity.ok(ApiResponse.ok("삭제 완료"));
    }
}
