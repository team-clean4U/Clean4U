package org.example.clean4u.workschedule;

import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.AuthService;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        boolean isSick = reason == ScheduleReason.병결;
        model.addAttribute("isSick", isSick);
        model.addAttribute("reason", reason);

        return "employee/save-form";
    }

    @PostMapping("/schedule")
    public String saveProc(WorkScheduleRequest.SaveDTO saveDTO) {

        if (saveDTO.isSick()) {
            workScheduleOverrideService.saveOverride(saveDTO);
        } else {
            workScheduleService.saveNormal(saveDTO);
        }

        return "redirect:/schedule/list";
    }

    @GetMapping("/schedule/list")
    public String scheduleList(Model model) {
        List<WorkScheduleResponse.ListDTO> scheduleList = workScheduleService.scheduleList();
        model.addAttribute("scheduleList", scheduleList);

        return "employee/schedule-list-form";
    }

    @GetMapping("/schedule/{scheduleId}/detail")
    public String scheduleDetail(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleResponse.DetailDTO schedule = workScheduleService.detail(scheduleId);

        model.addAttribute("schedule", schedule);

        return "employee/schedule-detail";
    }

    @GetMapping("/schedule/{scheduleId}/update")
    public String scheduleUpdateForm(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleResponse.UpdateDTO schedule = workScheduleService.scheduleUpdate(scheduleId);

        model.addAttribute("schedule", schedule);

        return "employee/schedule-update-form";
    }

    @PostMapping("/schedule/{scheduleId}/update")
    public String scheduleUpdateProc(
            @PathVariable Long scheduleId,
            WorkScheduleRequest.UpdateDTO updateDTO,
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
