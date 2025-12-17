package org.example.clean4u.employee;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception403;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/employee/join-list")
    public String join(Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        if (sessionUser == null || !sessionUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new Exception403("직원 관리 권한이 없습니다.");
        }

//        model.addAttribute("joinList", authService.joinList());

        return "employe/join-list";
    }

}
