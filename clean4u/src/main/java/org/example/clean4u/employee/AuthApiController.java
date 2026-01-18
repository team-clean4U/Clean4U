package org.example.clean4u.employee;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class AuthApiController {

    private final AuthService authService;

    @GetMapping("/admin/option-chart")
    public List<Object[]> optionChart() {
        return authService.optionChart();
    }

    @GetMapping("/admin/category-revenue-chart")
    public List<Object[]> categoryChart() { return authService.categoryChart(); }

    @GetMapping("/admin/monthly-trend-chart")
    public List<Object[]> salesChart() { return authService.salesChart(); }
}
