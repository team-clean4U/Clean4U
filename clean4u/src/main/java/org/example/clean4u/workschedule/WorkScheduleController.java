package org.example.clean4u.workschedule;

import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.AuthService;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeResponse;
import org.example.clean4u.employee.EmployeeService;
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
            Model model
    ) {
        Employee employee = authService.findById(employeeId);
        model.addAttribute("employee", employee);

        return "employee/save-form";
    }

    @PostMapping("/schedule")
    public String saveProc(WorkScheduleRequest.SaveDTO saveDTO) {
        workScheduleService.saveProc(saveDTO);

        return "redirect:/schedule/list";
    }

    @GetMapping("/schedule/list")
    public String scheduleList(Model model) {
        List<WorkScheduleResponse.ListDTO> scheduleList = workScheduleService.scheduleList();
        model.addAttribute("scheduleList", scheduleList);

        return "employee/schedule-list-form";
    }

    @GetMapping("/schedule/{scheduleId}/detail")
    public String detail(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleResponse.DetailDTO schedule = workScheduleService.detail(scheduleId);

        model.addAttribute("schedule", schedule);

        return "employee/schedule-detail";
    }

    @GetMapping("/schedule/{scheduleId}/update")
    public String updateForm(
            @PathVariable Long scheduleId,
            Model model
    ) {
        WorkScheduleResponse.UpdateDTO schedule = workScheduleService.update(scheduleId);

        model.addAttribute("schedule", schedule);

        return "employee/update-form";
    }

    @PostMapping("/schedule/{scheduleId}/update")
    public String updateProc(
            @PathVariable Long scheduleId,
            WorkScheduleRequest.UpdateDTO updateDTO,
            Model model
    ) {
        WorkSchedule schedule = workScheduleService.updateProc(scheduleId, updateDTO);

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
