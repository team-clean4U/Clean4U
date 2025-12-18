package org.example.clean4u.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CustomerController {
    private final CustomerService customerService;

    // 고객 생성 화면
    // http://localhost:8080/customer/list
    @GetMapping("/customer/save")
    public String saveForm(Model model) {
        CustomerResponse.SaveDTO dto = new CustomerResponse.SaveDTO();
        model.addAttribute("grade", Grade.NEW);
        return "customer/create-form";
    }

    // 생성 요청
    @PostMapping("/customer/save")
    public String saveProc(@Valid CustomerRequest.SaveDTO dto) {
        customerService.save(dto);
        
        return "redirect:/customer/list";
    }

    // 고객 전체 리스트
    @GetMapping("/customer/list")
    public String customerList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Model model
    ) {
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("category", category == null ? "" : category);

        List<CustomerResponse.ListDTO> customerList;

        if (keyword == null || keyword.trim().isBlank()) {
            customerList = customerService.getAllCustomers();
        } else if ("name".equalsIgnoreCase(category)) {
            customerList = customerService.searchByName(keyword.trim());
        } else if ("phone".equalsIgnoreCase(category)) {
            customerList = customerService.searchByPhone(keyword.trim());
        } else {
            customerList = customerService.getAllCustomers();
        }

        model.addAttribute("customerList", customerList);
        return "customer/list-form";
    }

    // 고객 단건 조회
    @GetMapping("/customer/{customerId}")
    public String detail(@PathVariable Long customerId, Model model) {
        CustomerResponse.DetailDTO customer = customerService.getDetail(customerId);

        model.addAttribute("customer", customer);

        return "customer/detail-form";
    }

    // 고객 수정 화면
    @GetMapping("/customer/{customerId}/update")
    public String updateForm(@PathVariable Long customerId, Model model) {
        CustomerResponse.UpdateDTO dto = customerService.getFormForUpdate(customerId);
        model.addAttribute("customer", dto);

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
}