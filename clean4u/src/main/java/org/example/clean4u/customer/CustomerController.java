package org.example.clean4u.customer;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class CustomerController {
    private final CustomerPersistRepository repository;

    // 고객 생성 화면
    @GetMapping("/customer/save")
    public String saveForm (HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");

        if (userSession == null) {
            throw new IllegalArgumentException("로그인 후 사용 가능합니다.");
        }

        return "redirect:/";
    }

    // 생성 요청
    @PostMapping("/customer/save")
    public String saveProc (CustomerRequest.saveDto dto, HttpSession session) {
        // 1. 로그인 - 인증
        Employee userSession = (Employee) session.getAttribute("sessionUser");

        if (userSession == null) {
            throw new IllegalArgumentException("로그인 후 사용 가능합니다.");
        }

        Customer customer = dto.toEntity();
        repository.save(customer);

        return "redirect:/";
    }

    @GetMapping("/customer")
    public String customerList(Model model) {

    }

}
