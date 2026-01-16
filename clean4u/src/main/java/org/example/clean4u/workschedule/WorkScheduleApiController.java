package org.example.clean4u.workschedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class WorkScheduleApiController {

    private final WorkScheduleService workScheduleService;

    @PutMapping("/schedules/{scheduleId}/edit")
    public String scheduleUpdateProc(
            @PathVariable Long scheduleId,
            @Valid WorkScheduleRequest.UpdateDTO updateDTO,
            Model model
    ) {
        WorkSchedule schedule = workScheduleService.scheduleUpdateProc(scheduleId, updateDTO);

        model.addAttribute("schedule", schedule);

        return "redirect:/schedules";
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public String delete(
            @PathVariable Long scheduleId
    ) {
        workScheduleService.delete(scheduleId);

        return "redirect:/schedules";
    }
}
