package org.example.clean4u.workschedule;

import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.AuthService;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeResponse;
import org.example.clean4u.employee.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @GetMapping("/schedule")
    public String employeeList(
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        List<EmployeeResponse.SimpleDTO> employeeList = workScheduleService.searchByName(keyword);
        model.addAttribute("employeeList", employeeList);
        model.addAttribute("keyword", keyword != null ? keyword : "");

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
}
