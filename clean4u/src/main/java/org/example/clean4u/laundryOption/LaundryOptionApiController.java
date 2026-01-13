package org.example.clean4u.laundryOption;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/laundry-options")
@RequiredArgsConstructor
public class LaundryOptionApiController {

    private final LaundryOptionService service;

    // PUT /api/v1/laundry-options/{laundryOptionId}
    @PutMapping("/{laundryOptionId}")
    public ResponseEntity<ApiResponse<LaundryOptionResponse.DetailDTO>> updateLaundryOption(
            @PathVariable Long laundryOptionId,
            @Valid @RequestBody LaundryOptionRequest.UpdateDTO updateDTO
    ) {
        service.update(laundryOptionId, updateDTO);
        LaundryOptionResponse.DetailDTO result = service.getDetail(laundryOptionId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
