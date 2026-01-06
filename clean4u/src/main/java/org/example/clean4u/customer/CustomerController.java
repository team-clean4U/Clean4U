package org.example.clean4u.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
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
    // http://localhost:8080/customer/list
    @GetMapping("/customer/save")
    public String saveForm(Model model) {
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/detail.css", "/css/customer.css"));
        CustomerResponse.SaveDTO dto = new CustomerResponse.SaveDTO();
        model.addAttribute("grade", Grade.NEW);
        return "customer/save-form";
    }

    // 생성 요청
    @PostMapping("/customer/save")
    public String saveProc(@Valid CustomerRequest.SaveDTO dto) {
        customerService.save(dto);
        
        return "redirect:/customer/list";
    }

    // 고객 전체 리스트
    @GetMapping("/customer/list")
    public String customerList(Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size
   ) {

        int pageIndex = Math.max(0, page -1);

        PageResponse<CustomerResponse.ListDTO> customerListPage = customerService.getAllCustomersWithSearch(pageIndex, size, keyword, category);

        boolean hasCategory = category != null && !category.isBlank();
        model.addAttribute("hasCategory", hasCategory);

        model.addAttribute("customerPage", customerListPage);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("category", category == null ? "all" :category);
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/customer.css"));

        return "customer/list-form";
    }

    // 고객 단건 조회
    @GetMapping("/customer/{customerId}")
    public String detail(@PathVariable Long customerId, Model model) {
        CustomerResponse.DetailDTO customer = customerService.getDetail(customerId);

        model.addAttribute("customer", customer);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/customer.css"));

        return "customer/detail-form";
    }

    // 고객 수정 화면
    @GetMapping("/customer/{customerId}/update")
    public String updateForm(@PathVariable Long customerId, Model model) {
        CustomerResponse.UpdateDTO dto = customerService.getFormForUpdate(customerId);
        model.addAttribute("customer", dto);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/customer.css"));

        return "customer/update-form";
    }

    @PostMapping("/customer/{customerId}/update")
    public String updateProc(@PathVariable Long customerId,
                             @Valid CustomerRequest.UpdateDTO updateDTO) {

        customerService.update(customerId, updateDTO);

        return "redirect:/customer/" + customerId;
    }

    // 고객 삭제
    @PostMapping("/customer/{customerId}/delete")
    public String deleteById(@PathVariable Long customerId) {
        customerService.delete(customerId);
        return "redirect:/customer/list";
    }

    @PostMapping("/customer/{customerId}/deactivation")
    public String deactivateById(@PathVariable Long customerId) {
        customerService.deactivateCustomer(customerId);
        return "redirect:/customer/list";
    }
}