package org.example.clean4u.workschedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class WorkScheduleOverrideApiController {

    private final WorkScheduleOverrideService workScheduleOverrideService;

    @PutMapping("/schedule-overrides/{overrideId}/edit")
    public ResponseEntity<ApiResponse<WorkScheduleOverrideResponse.UpdateDTO>> overrideUpdateProc(
            @PathVariable Long overrideId,
            @RequestBody @Valid WorkScheduleOverrideRequest.UpdateDTO updateDTO
    ) {
        WorkScheduleOverride override = workScheduleOverrideService.overrideUpdateProc(overrideId, updateDTO);
        WorkScheduleOverrideResponse.UpdateDTO response = new WorkScheduleOverrideResponse.UpdateDTO(override);

        return ResponseEntity.ok().body(ApiResponse.ok(response));
    }

    @DeleteMapping("/schedule-overrides/{overrideId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long overrideId
    ) {
        workScheduleOverrideService.delete(overrideId);

        return ResponseEntity.ok(ApiResponse.ok("예외스케줄 삭제가 완료되었습니다."));
    }
}
