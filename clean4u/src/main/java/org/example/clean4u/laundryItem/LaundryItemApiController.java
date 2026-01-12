package org.example.clean4u.laundryItem;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/laundry-items")
@RequiredArgsConstructor
public class LaundryItemApiController {

    private final LaundryItemService service;

    // PUT /api/v1/laundry-items/{laundryItemId}
    @PutMapping("/{laundryItemId}")
    public ResponseEntity<ApiResponse<LaundryItemResponse.DetailDTO>> updateLaundryItem(
            @PathVariable Long laundryItemId,
            @Valid @RequestBody LaundryItemRequest.UpdateDTO updateDTO
    ) {
        service.update(laundryItemId, updateDTO);
        LaundryItemResponse.DetailDTO result = service.getDetail(laundryItemId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // DELETE /api/v1/laundry-items/{laundryItemId}
    @DeleteMapping("/{laundryItemId}")
    public ResponseEntity<ApiResponse<Void>> deleteLaundryItem(@PathVariable Long laundryItemId) {
        service.delete(laundryItemId);
        return ResponseEntity.ok(ApiResponse.ok("세탁물이 삭제되었습니다"));
    }
}
