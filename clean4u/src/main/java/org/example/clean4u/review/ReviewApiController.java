package org.example.clean4u.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService service;

    // POST /api/review
    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestParam String token,
            @Valid @RequestBody ReviewRequest.SaveDTO saveDTO
    ) {
        service.save(saveDTO, token);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
