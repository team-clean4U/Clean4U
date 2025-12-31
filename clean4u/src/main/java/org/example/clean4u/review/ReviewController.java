package org.example.clean4u.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @GetMapping("/review/save")
    public String saveForm(@RequestParam String token, Model model) {
        service.validateToken(token);
        model.addAttribute("token", token);
        return "review/save-form";
    }

    @PostMapping("/review/save")
    public String saveProc(@RequestParam String token, @Valid ReviewRequest.SaveDTO dto) {
        service.save(dto, token);
        return "review/complete-form";
    }
}

