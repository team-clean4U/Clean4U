package org.example.clean4u.laundryOption;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/laundry-options")
@RequiredArgsConstructor
public class LaundryOptionApiController {

    private final LaundryOptionService service;

    // GET /api/laundry-options
    @GetMapping
    public ResponseEntity<PageResponse<LaundryOptionResponse.ListDTO>> getLaundryOptionList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String name
    ) {
        int pageIndex = Math.max(0, page - 1);
        PageResponse<LaundryOptionResponse.ListDTO> result = service.laundryOptionList(pageIndex, size, isActive, name);
        return ResponseEntity.ok().body(result);
    }

    // GET /api/laundry-options/{id}
    @GetMapping("/{id}")
    public ResponseEntity<LaundryOptionResponse.DetailDTO> getLaundryOptionById(@PathVariable Long id) {
        LaundryOptionResponse.DetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }

    // POST /api/laundry-options
    @PostMapping
    public ResponseEntity<?> createLaundryOption(
            @Valid @RequestBody LaundryOptionRequest.SaveDTO saveDTO
    ) {
        service.save(saveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // PUT /api/laundry-options/{id}
    @PutMapping("/{id}")
    public ResponseEntity<LaundryOptionResponse.DetailDTO> updateLaundryOption(
            @PathVariable Long id,
            @Valid @RequestBody LaundryOptionRequest.UpdateDTO updateDTO
    ) {
        service.update(id, updateDTO);
        LaundryOptionResponse.DetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }

    // PATCH /api/laundry-options/{id}/deactivate
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<LaundryOptionResponse.DetailDTO> deactivateLaundryOption(@PathVariable Long id) {
        service.deactivate(id);
        LaundryOptionResponse.DetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }

    // PATCH /api/laundry-options/{id}/activate
    @PatchMapping("/{id}/activate")
    public ResponseEntity<LaundryOptionResponse.DetailDTO> activateLaundryOption(@PathVariable Long id) {
        service.activate(id);
        LaundryOptionResponse.DetailDTO result = service.getDetail(id);
        return ResponseEntity.ok().body(result);
    }
}
