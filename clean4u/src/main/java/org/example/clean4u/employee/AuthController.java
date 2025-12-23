package org.example.clean4u.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/employee/join-list")
    public String join(Model model) {

        List<EmployeeResponse.JoinListDTO> joinList = authService.joinList();
        model.addAttribute("joinList", joinList);

        return "employee/join-list";
    }

    @GetMapping("/employee/list")
    public String list(Model model) {

        List<EmployeeResponse.ListDTO> employeeList = authService.list();
        model.addAttribute("employeeList", employeeList);

        return "employee/employee-list";
    }

    @GetMapping("/employee/{employeeId}/detail")
    private String detail(
            @PathVariable Long employeeId,
            Model model
    ) {
        EmployeeResponse.DetailDTO detail = authService.detail(employeeId);
        model.addAttribute("detail", detail);

        return "employee/employee-detail";
    }

    @PostMapping("/employee/{employeeId}/approve")
    public String approve(@PathVariable Long employeeId) {

        authService.approve(employeeId);
        return "redirect:/employee/join-list";
    }

    @PostMapping("/employee/{employeeId}/reject")
    public String reject(@PathVariable Long employeeId) {

        authService.reject(employeeId);
        return "redirect:/employee/join-list";
    }

    @PostMapping("/employee/{employeeId}/delete")
    public String delete(@PathVariable Long employeeId) {

        authService.delete(employeeId);
        return "redirect:/employee/list";
    }
}
