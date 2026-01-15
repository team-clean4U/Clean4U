package org.example.clean4u.customer;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    // 고객 생성 화면
    @GetMapping("/customers/new")
    public String saveForm(Model model) {
        CustomerResponse.SaveDTO dto = new CustomerResponse.SaveDTO();
        model.addAttribute("grade", Grade.NEW);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/detail.css", "/css/customer.css"));
        return "customer/save-form";
    }

    // 생성 요청
    @PostMapping("/customers/new")
    public String saveProc(@Valid CustomerRequest.SaveDTO dto, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        customerService.save(dto, sessionUser.getId());
        
        return "redirect:/customers";
    }

    // 고객 전체 리스트
    @GetMapping("/customers")
    public String customerList(Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
                               HttpSession session
   ) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        int pageIndex = Math.max(0, page -1);

        PageResponse<CustomerResponse.ListDTO> customerListPage;

        if (sessionUser.isAdmin()) {
            customerListPage = customerService.getAllCustomersWithSearch(pageIndex, size, keyword, category);
        } else {
            customerListPage = customerService.getAllCustomersForEmployee(pageIndex, size, keyword, category);
        }

        boolean hasCategory = category != null && !category.isBlank();
        model.addAttribute("hasCategory", hasCategory);

        model.addAttribute("customerPage", customerListPage);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("category", category == null ? "all" :category);
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/customer.css"));

        return "customer/list-form";
    }

    // 고객 단건 조회
    @GetMapping("/customers/{customerId}")
    public String detail(@PathVariable Long customerId, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        CustomerResponse.DetailDTO customer = customerService.getDetail(customerId);

        model.addAttribute("customer", customer);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/customer.css", "/css/order.css"));

        return "customer/detail-form";
    }

    // 고객 수정 화면
    @GetMapping("/customers/{customerId}/edit")
    public String updateForm(@PathVariable Long customerId, Model model) {
        CustomerResponse.UpdateDTO dto = customerService.getFormForUpdate(customerId);
        model.addAttribute("customer", dto);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/customer.css"));

        return "customer/update-form";
    }

    @PostMapping("/customers/{customerId}/status")
    public String updateStatus(@PathVariable Long customerId, @RequestParam(defaultValue = "true") boolean isActive) {
        if (isActive) {
            customerService.activateCustomer(customerId);
        } else {
            customerService.deactivateCustomer(customerId);
        }

        return "redirect:/customers/" + customerId;
    }
}