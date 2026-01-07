package org.example.clean4u.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @GetMapping("/r/{token}")
    public String redirectToReview(@PathVariable String token) {
        return "redirect:/review/save?token=" + token;
    }

    @GetMapping("/review/save")
    public String saveForm(@RequestParam String token, Model model) {
        service.validateToken(token);
        model.addAttribute("token", token);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css", "/css/review.css"));
        return "review/save-form";
    }

    @PostMapping("/review/save")
    public String saveProc(@RequestParam String token, @Valid ReviewRequest.SaveDTO dto, Model model) {
        service.save(dto, token);
        model.addAttribute("additionalCss", Arrays.asList("/css/review.css"));
        return "review/complete-form";
    }
}

