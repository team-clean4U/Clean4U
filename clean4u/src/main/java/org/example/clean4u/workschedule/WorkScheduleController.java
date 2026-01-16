package org.example.clean4u.workschedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.AuthService;
import org.example.clean4u.employee.Employee;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;
    private final WorkScheduleOverrideService workScheduleOverrideService;
    private final AuthService authService;

    @PostMapping("/schedules")
    public String saveProc(@Valid WorkScheduleRequest.SaveDTO saveDTO) {

        if (saveDTO.isSick()) {
            workScheduleOverrideService.saveOverride(saveDTO);
            return "redirect:/schedule-overrides";
        } else {
            workScheduleService.saveNormal(saveDTO);
            return "redirect:/schedules";
        }
    }

    @GetMapping("/schedules")
    public String scheduleList(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime searchStartTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime searchEndTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        int pageIndex = Math.max(0, page - 1);

        PageResponse<WorkScheduleResponse.ListDTO> scheduleListPage =
                workScheduleService.getAllScheduleWithSearch(pageIndex, size, keyword, category, searchStartTime, searchEndTime);

        boolean hasCategory = category != null && !category.isBlank();
        model.addAttribute("hasCategory", hasCategory);

        model.addAttribute("scheduleListPage", scheduleListPage);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("category", category == null ? "all" : category);
        model.addAttribute("searchStartTime", searchStartTime != null ?  searchStartTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
        model.addAttribute("searchEndTime", searchEndTime != null ? searchEndTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
        model.addAttribute("isName", "name".equalsIgnoreCase(category));
        model.addAttribute("isUserName", "username".equalsIgnoreCase(category));
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/schedule.css", "/css/order.css"));

        return "workschedule/schedule-list-form";
    }

    @GetMapping("/schedules/{employeeId:\\d+}/new")
    public String saveForm(
            @PathVariable Long employeeId,
            @RequestParam(required = false) ScheduleReason reason,
            Model model
    ) {
        Employee employee = authService.findById(employeeId);
        model.addAttribute("employee", employee);

        List<Employee> overrideList = authService.getOverrideEmployee(employee);
        model.addAttribute("overrideList", overrideList);

        boolean isSick = reason == ScheduleReason.SICK;
        model.addAttribute("isSick", isSick);
        model.addAttribute("reason", reason);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/schedule.css", "/css/employee-search.css"));

        return "workschedule/save-form";
    }

    @GetMapping("/schedules/{scheduleId}")
    public String scheduleDetail(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleResponse.DetailDTO schedule = workScheduleService.detail(scheduleId);

        model.addAttribute("schedule", schedule);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/schedule.css"));

        return "workschedule/schedule-detail";
    }

    @GetMapping("/schedules/{scheduleId}/edit")
    public String scheduleUpdateForm(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleResponse.UpdateDTO schedule = workScheduleService.scheduleUpdate(scheduleId);

        model.addAttribute("schedule", schedule);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/schedule.css"));

        return "workschedule/schedule-update-form";
    }
}
