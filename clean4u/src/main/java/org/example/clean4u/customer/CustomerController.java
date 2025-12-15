package org.example.clean4u.customer;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception401;
import org.example.clean4u.employee.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class CustomerController {
    private final CustomerRepository repository;

    // 고객 생성 화면
    // http://localhost:8080/customer/list
    @GetMapping("/customer/save")
    public String saveForm(HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");

        if (userSession == null) {
            throw new Exception401("로그인 후 사용 가능합니다.");
        }

        return "customer/create-form";
    }

    // 생성 요청
    @PostMapping("/customer/save")
    public String saveProc(CustomerRequest.saveDto dto, HttpSession session) {
        // 1. 로그인 - 인증
        Employee userSession = (Employee) session.getAttribute("sessionUser");

        if (userSession == null) {
            throw new Exception401("로그인 후 사용 가능합니다.");
        }

        Customer customer = dto.toEntity();
        repository.save(customer);

        return "redirect:/";
    }

    // 고객 전체 리스트
    @GetMapping("/customer/list")
    public String customerList(Model model, HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");
        if (userSession == null) {
            throw new Exception401("로그인 후 사용 가능합니다.");
        }

        List<Customer> customerList = repository.findAll();
        model.addAttribute("customerList", customerList);
        return "customer/list-form";
    }

    // 고객 단건 조회
    @GetMapping("/customer/{id}")
    public String getCustomerById(@PathVariable Long id, Model model, HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");
        if (userSession == null) {
            throw new Exception401("로그인 후 사용 가능합니다.");
        }

        Customer customer = repository.findById(id);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        model.addAttribute("customer", customer);
        model.addAttribute("memo", customer.getMemo() == null ? "-" : customer.getMemo());
        model.addAttribute("createdAt", customer.getCreatedAt().toLocalDateTime().format(formatter));

        return "customer/detail-form";
    }

    // 고객 수정
    @GetMapping("/customer/{id}/update")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");
        if (userSession == null) {
            throw new Exception401("로그인 후 사용 가능합니다.");
        }

        Customer customer = repository.findById(id);
        if (customer == null) {
            throw new IllegalArgumentException("고객이 존재하지 않습니다.");
        }

        model.addAttribute("customer", customer);

        return "customer/update";
    }

    // 고객 삭제
    @GetMapping("/customer/{id}/delete")
    public String deleteById(@PathVariable Long id, HttpSession session) {
        Employee userSession = (Employee) session.getAttribute("sessionUser");
        if (userSession == null) {
            throw new Exception401("로그인 후 사용 가능합니다.");
        }

        // 관리자 권한 체크 해야함
        repository.findById(id);

        return "redirect:customer/list";

    }
}