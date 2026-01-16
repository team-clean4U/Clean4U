package org.example.clean4u.workschedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class WorkScheduleOverrideController {

    private final WorkScheduleOverrideService workScheduleOverrideService;

    @GetMapping("/schedule-overrides")
    public String overrideList(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        int pageIndex = Math.max(0, page - 1);

        PageResponse<WorkScheduleOverrideResponse.ListDTO> scheduleListPage =
                workScheduleOverrideService.getAllScheduleWithSearch(
                        pageIndex, size, keyword, category, startTime, endTime, date);

        boolean hasCategory = category != null && !category.isBlank();
        model.addAttribute("hasCategory", hasCategory);

        model.addAttribute("scheduleListPage", scheduleListPage);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("category", category == null ? "all" : category);
        model.addAttribute("startTime", startTime != null ?  startTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
        model.addAttribute("endTime", endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
        model.addAttribute("date", date != null ? date : LocalDate.now());
        model.addAttribute("isOriginalName", "originalName".equalsIgnoreCase(category));
        model.addAttribute("isOverrideName", "overrideName".equalsIgnoreCase(category));
        model.addAttribute("isDate", "date".equalsIgnoreCase(category));
        model.addAttribute("isTime", "time".equalsIgnoreCase(category));
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/schedule.css", "/css/order.css"));

        return "workschedule/override-list-form";
    }

    @GetMapping("/schedule-overrides/{scheduleId}")
    public String overrideDetail(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleOverrideResponse.DetailDTO override = workScheduleOverrideService.overrideDetail(scheduleId);

        model.addAttribute("override", override);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/schedule.css"));

        return "workschedule/override-detail";
    }

    @GetMapping("/schedule-overrides/{scheduleId}/edit")
    public String overrideUpdateForm(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleOverrideResponse.UpdateDTO schedule = workScheduleOverrideService.overrideUpdateForm(scheduleId);

        model.addAttribute("schedule", schedule);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/schedule.css"));

        return "workschedule/override-update-form";
    }

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
    public String delete(
            @PathVariable Long scheduleId
    ) {
        workScheduleOverrideService.delete(scheduleId);

        return "redirect:/schedule-overrides";
    }
}
