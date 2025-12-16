package org.example.clean4u.employee;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/join")
    public String join() {

        return "user/join-form";
    }

    @PostMapping("/join")
    public String joinProc(@Valid EmployeeRequest.JoinDTO joinDTO) {
        employeeService.join(joinDTO);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login-form";
    }

    @PostMapping("/login")
    public String loginProc(EmployeeRequest.LoginDTO loginDTO, HttpSession session) {
        try {
            Employee sessionUser = employeeService.login(loginDTO);
            session.setAttribute("sessionUser", sessionUser);
            return "redirect:/main";
        } catch (Exception e) {
            return "user/login-form";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/main")
    public String dashboard(HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (sessionUser.getUserRole() == UserRole.ADMIN) {
            return "user/dashboard-admin";
        }

        return "user/dashboard-employee";
    }

    @GetMapping("/employee/update")
    public String update(Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        Employee employee = employeeService.update(sessionUser.getId());
        model.addAttribute("employee", employee);

        return "user/update-form";
    }

    @PostMapping("/employee/update")
    public String updateProc(@Valid EmployeeRequest.UpdateDTD updateDTD, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        try {
            Employee updateEmployee = employeeService.updateProc(updateDTD, sessionUser.getId());
            session.setAttribute("sessionUser", updateEmployee);
            return "redirect:/main";
        } catch (Exception e) {
            return "user/update-form";
        }
    }
}
