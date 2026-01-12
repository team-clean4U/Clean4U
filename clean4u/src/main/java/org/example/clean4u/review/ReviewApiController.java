package org.example.clean4u.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService service;

    // POST /api/v1/review
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createReview(
            @RequestParam String token,
            @Valid @RequestBody ReviewRequest.SaveDTO saveDTO
    ) {
        service.save(saveDTO, token);
        return ResponseEntity.ok(ApiResponse.ok("리뷰가 작성되었습니다."));
    }
}
