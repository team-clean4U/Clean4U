package org.example.clean4u.laundryItem;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/laundry-items")
@RequiredArgsConstructor
public class LaundryItemApiController {

    private final LaundryItemService service;

    // GET /api/laundry-items
    @GetMapping
    public ResponseEntity<PageResponse<LaundryItemResponse.ListDTO>> getLaundryItemList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) LaundryCategory category,
            @RequestParam(required = false) String name
    ) {
        int pageIndex = Math.max(0, page - 1);
        PageResponse<LaundryItemResponse.ListDTO> result = service.laundryItemList(pageIndex, size, category, name);
        return ResponseEntity.ok().body(result);
    }

    // GET /api/laundry-items/{id}
    @GetMapping("/{id}")
    public ResponseEntity<LaundryItemResponse.DetailDTO> getLaundryItemById(@PathVariable Long id) {
        LaundryItemResponse.DetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }

    // POST /api/laundry-items
    @PostMapping
    public ResponseEntity<?> createLaundryItem(
            @Valid @RequestBody LaundryItemRequest.SaveDTO saveDTO
    ) {
        service.save(saveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // PUT /api/laundry-items/{id}
    @PutMapping("/{id}")
    public ResponseEntity<LaundryItemResponse.DetailDTO> updateLaundryItem(
            @PathVariable Long id,
            @Valid @RequestBody LaundryItemRequest.UpdateDTO updateDTO
    ) {
        service.update(id, updateDTO);
        LaundryItemResponse.DetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }

    // DELETE /api/laundry-items/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLaundryItem(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().body(Map.of("message", "세탁물이 삭제되었습니다"));
    }
}
