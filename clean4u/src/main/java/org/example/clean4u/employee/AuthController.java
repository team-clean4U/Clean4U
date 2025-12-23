package org.example.clean4u.employee;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception403;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/employee/join-list")
    public String join(Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null || !sessionUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new Exception403("직원 관리 권한이 없습니다.");
        }

        model.addAttribute("joinList", authService.joinList());

        return "employee/join-list";
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
}
