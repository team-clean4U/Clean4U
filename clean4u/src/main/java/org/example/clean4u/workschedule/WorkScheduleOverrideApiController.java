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

    @PutMapping("/schedule-overrides/{scheduleId}/edit")
    public String overrideUpdateProc(
            @PathVariable Long scheduleId,
            @Valid WorkScheduleOverrideRequest.UpdateDTO updateDTO,
            Model model
    ) {
        WorkScheduleOverride override = workScheduleOverrideService.overrideUpdateProc(scheduleId, updateDTO);

        model.addAttribute("override", override);

        return "redirect:/schedule-overrides";
    }

    @DeleteMapping("/schedule-overrides/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long scheduleId
    ) {
        workScheduleOverrideService.delete(scheduleId);

        return ResponseEntity.ok(ApiResponse.ok("예외스케줄 삭제가 완료되었습니다."));
    }
}
