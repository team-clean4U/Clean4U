package org.example.clean4u.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AuthApiController {

    private final AuthService authService;

    @GetMapping("/admin/option-chart")
    public List<Object[]> optionChart() {
        return authService.optionChart();
    }
}
