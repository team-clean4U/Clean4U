package org.example.clean4u.customer;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CustomerController {
    private final CustomerRepository repository;

    // 고객 생성 화면
    // http://localhost:8080/customer/save
    @GetMapping("/customer/save")
    public String saveForm (HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");

        if (userSession == null) {
            throw new IllegalArgumentException("로그인 후 사용 가능합니다.");
        }

        return "customer/create-form";
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
    public String customerList(Model model, HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");
        if (userSession == null) {
            throw new IllegalArgumentException("로그인 후 사용 가능합니다.");
        }

        List<Customer> customerList = repository.findAll();
        model.addAttribute("customerList", customerList);
        return "customer/list";
    }

}
