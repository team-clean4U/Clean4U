package org.example.clean4u.employee;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception403;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final HttpSession session;
    private final EmployeeService employeeService;
    private final AuthService authService;
    private final DashboardService dashboardService;
    private final WorkScheduleService workScheduleService;
    private final WorkScheduleOverrideService workScheduleOverrideService;

    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/user.css"));
        return "user/join-form";
    }

    @PostMapping("/join")
    public String joinProc(@Valid EmployeeRequest.JoinDTO joinDTO) {
        employeeService.join(joinDTO);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/user.css"));
        return "user/login-form";
    }

    @PostMapping("/login")
    public String loginProc(EmployeeRequest.LoginDTO loginDTO, HttpSession session) {
        try {
            Employee sessionUser = employeeService.login(loginDTO);
            session.setAttribute("sessionUser", sessionUser);
            return "redirect:/main";
        } catch (Exception400 e) {
            throw e;
        } catch (Exception404 e) {
            throw e;
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/main")
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

    @GetMapping("/me/update")
    public String update(Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        Employee employee = employeeService.update(sessionUser.getId());
        model.addAttribute("employee", employee);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/user.css"));

        return "user/update-form";
    }

    @PostMapping("/me/update")
    public String updateProc(@Valid EmployeeRequest.UpdateDTD updateDTD, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        try {
            Employee updateEmployee = employeeService.updateProc(updateDTD, sessionUser.getId());
            session.setAttribute("sessionUser", updateEmployee);
            return "redirect:/main";
        } catch (Exception403 e) {
            throw e;
        } catch (Exception404 e) {
            throw e;
        }
    }

    @GetMapping("/password")
    public String findPasswordForm(Model model) {
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/user.css"));

        return "user/password-form";
    }

    @PostMapping("/password")
    public String findPasswordProc(@Valid EmployeeRequest.FindPassword findPassword) {
        employeeService.findPassword(findPassword);

        return "redirect:/login";
    }

    @GetMapping("/password/reset")
    public String passwordResetForm(@RequestParam String token, Model model) {
        String email = (String) session.getAttribute("reset_token_" + token);
        LocalDateTime expire = (LocalDateTime) session.getAttribute("reset_token_expire" + token);

        if (email == null || LocalDateTime.now().isAfter(expire)) {
            throw new Exception400("유효하지 않거나 만료된 토큰입니다.");
        }

        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/user.css"));
        model.addAttribute("token", token);
        return "user/password-reset-form";
    }

    @PostMapping("/password/reset")
    public String passwordResetProc(@Valid EmployeeRequest.PasswordReset passwordReset) {

        return "redirect:/login";
    }
}
