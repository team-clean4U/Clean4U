package org.example.clean4u.employee;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/admin/employees/pending")
    public String join(Model model) {
        List<EmployeeResponse.JoinListDTO> joinList = authService.joinList();
        model.addAttribute("joinList", joinList);
        model.addAttribute("additionalCss", Arrays.asList("/css/employee-search.css", "/css/user.css"));

        return "employee/join-list";
    }

    @GetMapping("/admin/employees")
    public String list(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        int pageIndex = Math.max(0, page - 1);

        PageResponse<EmployeeResponse.ListDTO> employeePage = authService.getAllEmployeeWithSearch(pageIndex, size, keyword, category);

        boolean hasCategory = category != null && !category.isBlank();
        model.addAttribute("hasCategory", hasCategory);

        model.addAttribute("employeePage", employeePage);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("category", category == null ? "all" : category);
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/employee-search.css"));

        return "employee/employee-list";
    }

    @GetMapping("/admin/employees/{employeeId}")
    private String detail(
            @PathVariable Long employeeId,
            Model model
    ) {
        EmployeeResponse.DetailDTO detail = authService.detail(employeeId);
        model.addAttribute("detail", detail);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/employee-search.css"));

        return "employee/employee-detail";
    }

    @PostMapping("/admin/employees/{employeeId}")
    public String updateStatus(
            @PathVariable Long employeeId,
            @RequestParam String status
    ) {
        UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
        authService.updateStatus(employeeId, userStatus);
        return "redirect:/admin/employees/pending";
    }
}
