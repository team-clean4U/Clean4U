package org.example.clean4u.employee;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.dashboard.DashboardService;
import org.example.clean4u.workschedule.WorkScheduleOverrideService;
import org.example.clean4u.workschedule.WorkScheduleResponse;
import org.example.clean4u.workschedule.WorkScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final AuthService authService;
    private final DashboardService dashboardService;
    private final WorkScheduleService workScheduleService;
    private final WorkScheduleOverrideService workScheduleOverrideService;

    @GetMapping("/employees/new")
    public String join(Model model) {
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/user.css"));
        return "user/join-form";
    }

    @PostMapping("/employees/new")
    public String joinProc(@Valid EmployeeRequest.JoinDTO joinDTO, HttpSession session) {
        Boolean emailVerified = (Boolean) session.getAttribute("verified_email_" + joinDTO.getEmail());

        employeeService.join(joinDTO, emailVerified != null && emailVerified);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/user.css"));
        return "user/login-form";
    }

    @PostMapping("/login")
    public String loginProc(@Valid EmployeeRequest.LoginDTO loginDTO, HttpSession session) {
        try {
            Employee sessionUser = employeeService.login(loginDTO);
            session.setAttribute("sessionUser", sessionUser);
            return "redirect:/dashboard";
        } catch (Exception400 e) {
            throw e;
        } catch (Exception404 e) {
            throw e;
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        WorkScheduleResponse.DataDTO dataDTO = workScheduleService.today(sessionUser.getId());
        long countToday = workScheduleOverrideService.countTodayOverrides(sessionUser.getId());
        long countAllEmployees = authService.countAllEmployees();
        long countTodayOverrides = authService.countTodayOverrides();

        Map<String, Object> statistics = dashboardService.getStatistics();
        model.addAllAttributes(statistics);
        model.addAttribute("additionalCss", Arrays.asList("/css/user.css", "/css/order.css"));
        model.addAttribute("dataDTO", dataDTO);
        model.addAttribute("countToday", countToday);
        model.addAttribute("countAllEmployees", countAllEmployees);
        model.addAttribute("countTodayOverrides", countTodayOverrides);

        if (sessionUser.getUserRole() == UserRole.ADMIN) {
            long pendingCount = employeeService.pendingCount();
            model.addAttribute("pendingCount", pendingCount);
            return "user/dashboard-admin";
        }

        return "user/dashboard-employee";
    }

    @GetMapping("/schedules/employees")
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

    @GetMapping("/employees/me")
    public String update(Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        Employee employee = employeeService.update(sessionUser.getId());
        model.addAttribute("employee", employee);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/user.css"));

        return "user/update-form";
    }

    @GetMapping("/password")
    public String findPasswordForm(Model model) {
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/user.css"));

        return "user/password-form";
    }

    @PostMapping("/password")
    public String findPasswordProc(@Valid EmployeeRequest.FindPassword findPassword, HttpSession session) {

        ResetPasswordSession reset = employeeService.findPassword(findPassword);
        session.setAttribute("RESET_PASSWORD", reset);

        return "redirect:/login";
    }

    @GetMapping("/password/reset")
    public String passwordResetForm(@RequestParam String token, Model model) {

        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/user.css"));
        model.addAttribute("token", token);

        return "user/password-reset-form";
    }

    @PostMapping("/password/reset")
    public String passwordResetProc(@Valid EmployeeRequest.PasswordReset passwordReset, HttpSession session) {

        ResetPasswordSession reset = (ResetPasswordSession) session.getAttribute("RESET_PASSWORD");

        employeeService.passwordReset(passwordReset, reset);
        session.removeAttribute("RESET_PASSWORD");

        return "redirect:/login";
    }
}
