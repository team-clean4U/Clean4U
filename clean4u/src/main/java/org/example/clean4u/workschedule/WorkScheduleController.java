package org.example.clean4u.workschedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.AuthService;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/schedule")
    public String search(
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        List<EmployeeResponse.SimpleDTO> employeeList = workScheduleService.searchByName(keyword);
        model.addAttribute("employeeList", employeeList);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("additionalCss", Arrays.asList("/css/employee-search.css"));

        return "employee/employee-search";
    }

    @GetMapping("/schedule/{employeeId}")
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

    @PostMapping("/schedule")
    public String saveProc(@Valid WorkScheduleRequest.SaveDTO saveDTO) {

        if (saveDTO.isSick()) {
            workScheduleOverrideService.saveOverride(saveDTO);
            return "redirect:/override/list";
        } else {
            workScheduleService.saveNormal(saveDTO);
            return "redirect:/schedule/list";
        }
    }

    @GetMapping("/schedule/list")
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

    @GetMapping("/schedule/{scheduleId}/detail")
    public String scheduleDetail(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleResponse.DetailDTO schedule = workScheduleService.detail(scheduleId);

        model.addAttribute("schedule", schedule);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/schedule.css"));

        return "workschedule/schedule-detail";
    }

    @GetMapping("/schedule/{scheduleId}/update")
    public String scheduleUpdateForm(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleResponse.UpdateDTO schedule = workScheduleService.scheduleUpdate(scheduleId);

        model.addAttribute("schedule", schedule);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/schedule.css"));

        return "workschedule/schedule-update-form";
    }

    @PostMapping("/schedule/{scheduleId}/update")
    public String scheduleUpdateProc(
            @PathVariable Long scheduleId,
            @Valid WorkScheduleRequest.UpdateDTO updateDTO,
            Model model
    ) {
        WorkSchedule schedule = workScheduleService.scheduleUpdateProc(scheduleId, updateDTO);

        model.addAttribute("schedule", schedule);

        return "redirect:/schedule/list";
    }

    @PostMapping("/schedule/{scheduleId}/delete")
    public String delete(
            @PathVariable Long scheduleId
    ) {
        workScheduleService.delete(scheduleId);

        return "redirect:/schedule/list";
    }
}
