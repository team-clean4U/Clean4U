package org.example.clean4u.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class EmployeeController {
    @GetMapping("/login")
    public String login() {

        return "user/login-form";
    }

    @GetMapping("/join")
    public String join() {

        return "user/join-form";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {

        return "user/dashboard-admin";
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard() {

        return "user/dashboard-employee";
    }
}
