package org.example.clean4u.customer;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u.employee.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CustomerController {
    private final CustomerRepository repository;

    // 고객 생성 화면
    // http://localhost:8080/customer/save
    @GetMapping("/customer/save")
    public String saveForm(HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");

        if (userSession == null) {
            throw new IllegalArgumentException("로그인 후 사용 가능합니다.");
        }

        return "customer/create-form";
    }

    // 생성 요청
    @PostMapping("/customer/save")
    public String saveProc(CustomerRequest.saveDto dto, HttpSession session) {
        // 1. 로그인 - 인증
        Employee userSession = (Employee) session.getAttribute("sessionUser");

        if (userSession == null) {
            throw new IllegalArgumentException("로그인 후 사용 가능합니다.");
        }

        Customer customer = dto.toEntity();
        repository.save(customer);

        return "redirect:/";
    }

    // 고객 전체 리스트
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

    // 고객 단건 조회
    @GetMapping("/customer/{id}")
    public String getCustomerById(@PathVariable Long id, Model model, HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");
        if (userSession == null) {
            throw new IllegalArgumentException("로그인 후 사용 가능합니다.");
        }

        Customer customer = repository.findById(id);
        model.addAttribute("customer", customer);

        return "/customer/detail";
    }

    // 고객 수정
    @GetMapping("/customer/{id}/update")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");
        if (userSession == null) {
            throw new IllegalArgumentException("로그인 후 사용 가능합니다.");
        }

        Customer customer = repository.findById(id);
        if (customer == null) {
            throw new IllegalArgumentException("고객이 존재하지 않습니다.");

        }

        model.addAttribute("customer", customer);

        return "/";
    }
}