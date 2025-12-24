package org.example.clean4u.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WorkScheduleOverrideController {

    private final WorkScheduleOverrideService workScheduleOverrideService;

    @GetMapping("/override/list")
    public String overrideList(Model model) {
        List<WorkScheduleOverrideResponse.ListDTO> overrideList = workScheduleOverrideService.overrideList();
        model.addAttribute("overrideList", overrideList);

        return "workschedule/override-list-form";
    }

    @GetMapping("/override/{scheduleId}/detail")
    public String overrideDetail(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleOverrideResponse.DetailDTO override = workScheduleOverrideService.overrideDetail(scheduleId);

        model.addAttribute("override", override);

        return "workschedule/override-detail";
    }

    @GetMapping("/override/{scheduleId}/update")
    public String overrideUpdateForm(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleOverrideResponse.UpdateDTO schedule = workScheduleOverrideService.overrideUpdateForm(scheduleId);

        model.addAttribute("schedule", schedule);

        return "workschedule/override-update-form";
    }

    @PostMapping("/override/{scheduleId}/update")
    public String overrideUpdateProc(
            @PathVariable Long scheduleId,
            WorkScheduleOverrideRequest.UpdateDTO updateDTO,
            Model model
    ) {
        WorkScheduleOverride override = workScheduleOverrideService.overrideUpdateProc(scheduleId, updateDTO);

        model.addAttribute("override", override);

        return "redirect:/override/list";
    }

    @PostMapping("/override/{scheduleId}/delete")
    public String delete(
            @PathVariable Long scheduleId
    ) {
        workScheduleOverrideService.delete(scheduleId);

        return "redirect:/override/list";
    }
}
